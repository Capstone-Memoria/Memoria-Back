package ac.mju.memoria.backend.system.security.configurer;

public interface Customizer<T> {
    void customize(T t);
}