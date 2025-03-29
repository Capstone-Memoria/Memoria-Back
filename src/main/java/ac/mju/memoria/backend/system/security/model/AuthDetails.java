package ac.mju.memoria.backend.system.security.model;


import org.springframework.security.core.AuthenticatedPrincipal;

public abstract class AuthDetails implements AuthenticatedPrincipal {
    public abstract String getKey();

    public String getName() {
        return this.getKey();
    }
}
