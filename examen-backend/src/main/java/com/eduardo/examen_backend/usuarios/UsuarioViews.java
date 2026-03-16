package com.eduardo.examen_backend.usuarios;

public class UsuarioViews {

    public interface DiscreetUser {}

    public interface NotDiscreetUser extends DiscreetUser {}

    public interface IndiscreetUser extends DiscreetUser{}

    public interface ExtraIndiscreetUser extends IndiscreetUser{}

    public interface UltraExtraIndiscreetUser extends ExtraIndiscreetUser{}
}
