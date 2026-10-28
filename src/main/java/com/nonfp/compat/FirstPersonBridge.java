package com.nonfp.compat;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * Hooks into FirstPerson's activation API without a compile time dependency.
 *
 * <p>FirstPerson 2.7.x exposes
 * {@code FirstPersonAPI.getActivationHandlers()}, which returns the mutable list that
 * {@code LogicHandler.shouldApplyThirdPerson} walks every frame. Returning {@code true}
 * from {@code ActivationHandler.preventFirstperson()} makes FirstPerson keep the vanilla
 * first person view instead of drawing your own body.</p>
 */
public final class FirstPersonBridge {

    private static boolean installed;

    private FirstPersonBridge() {
    }

    @SuppressWarnings("unchecked")
    public static void install() throws ReflectiveOperationException {
        if (installed) {
            return;
        }

        ClassLoader loader = FirstPersonBridge.class.getClassLoader();
        Class<?> api = Class.forName("dev.tr7zw.firstperson.api.FirstPersonAPI", false, loader);
        Class<?> handlerType = Class.forName("dev.tr7zw.firstperson.api.ActivationHandler", false, loader);

        Object handler = Proxy.newProxyInstance(
                handlerType.getClassLoader(),
                new Class<?>[]{handlerType},
                new ActivationHandlerProxy());

        Method getter = api.getMethod("getActivationHandlers");
        Object handlers = getter.invoke(null);
        if (!(handlers instanceof List<?>)) {
            throw new IllegalStateException("FirstPersonAPI.getActivationHandlers() did not return a list");
        }
        ((List<Object>) handlers).add(handler);
        installed = true;
    }

    private static final class ActivationHandlerProxy implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            return switch (method.getName()) {
                case "preventFirstperson" -> AnimationProbe.isLocalPlayerAnimating();
                case "equals" -> proxy == (args == null || args.length == 0 ? null : args[0]);
                case "hashCode" -> System.identityHashCode(proxy);
                case "toString" -> "nonfpcompat:activation-handler";
                default -> defaultValue(method.getReturnType());
            };
        }

        private static Object defaultValue(Class<?> type) {
            if (!type.isPrimitive()) {
                return null;
            }
            if (type == boolean.class) {
                return Boolean.FALSE;
            }
            if (type == int.class) {
                return 0;
            }
            if (type == long.class) {
                return 0L;
            }
            if (type == float.class) {
                return 0.0F;
            }
            if (type == double.class) {
                return 0.0D;
            }
            if (type == byte.class) {
                return (byte) 0;
            }
            if (type == short.class) {
                return (short) 0;
            }
            if (type == char.class) {
                return (char) 0;
            }
            return null;
        }
    }
}
