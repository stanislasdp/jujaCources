package controller.web;

import controller.web.action.AbstractAction;
import controller.web.action.Action;
import lombok.SneakyThrows;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import service.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.google.common.collect.ImmutableList.toImmutableList;

@Deprecated
@Component
public class ActionsResolver {

    private List<Action> actions;

    @Autowired
    private Service service;

    @SneakyThrows
    public void resolve() {
        Reflections reflections = new Reflections(this.getClass().getPackage().getName());
        Set<Class<? extends AbstractAction>> classes = reflections.getSubTypesOf(AbstractAction.class);
        actions = classes.stream().map(clazz -> {
            try {
                return clazz.getConstructor(Service.class).newInstance(service);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                    IllegalArgumentException | InvocationTargetException | SecurityException e) {
                throw new RuntimeException(e);
            }
        }).collect(toImmutableList());
    }

    public Optional<Action> getAction(String action) {
        if (Objects.isNull(actions)) {
            resolve();
        }
        return actions.stream()
                .filter(act -> act.canProcess(action))
                .findFirst();
    }
}
