package com.tfg.slr.usersmicroservice.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MessageConstants {

    public static String SERVICE_DOWN = "No se ha obtenido respuesta del otro servicio";
    public static String USER_ACCOUNT_NULL_NOT_FOUND = "El ID de la cuenta de usuario es nulo o no existe.";
    public static String NULL_ENTITY = "La entidad o ID proporcionada es nula.";
    public static String NULL_USERNAME = "No se ha proporcionado un nombre de usuario.";
    public static String EXISTING_USER = "Ya existe un usuario con ese nombre.";
    public static String PASSWORD_HAS_ERRORS = "La contraseña es errónea: Debe tener entre 8 y 32 caracteres y debe contener al menos una letra mayúscula y un número.";
    public static String WRONG_PASSWORD = "La contraseña no es correcta";
    public static String DELETE_PROFILE_FIRST = "Debe eliminar el perfil antes de eliminar la cuenta de usuario.";
    public static String WRONG_EMAIL = "La dirección de email proporcionada no es correcta";
    public static String EXISTING_EMAIL = "Ya existe un perfil asociado a ese email";
    public static String USER_NULL_NOT_FOUND = "El ID del usuario es nulo o no existe.";
    public static String USERNAME_NOT_FOUND = "El nombre de usuario no existe.";
    public static String NO_USER_AUTHENTICATED = "No hay ningún usuario autenticado.";
    public static String PRINCIPAL_NOT_FOUND = "Error: No se encontró la cuenta del usuario princiapl autentica.";
    public static String INVALID_USERNAME_OR_PASSWORD = "El nombre de usuario o la contraseña son erróneos.";
    public static String USERNAME_CHANGED = "El nombre de usuario se ha cambiado correctamente.";
    public static String PASSWORD_CHANGED = "La contraseña se ha cambiado correctamente.";
    public static String WRONG_USERNAME = "El nombre de usuario no es correcto.";
    public static String INVALID_TOKEN = "El token no es válido.";
    public static String ACCOUNT_CREATED = "Cuenta creada correctamente para el usuario ";
    public static String USER_NOT_FOUND = "No se ha encontrado el perfil del usuario.";
    public static String PROFILE_DELETED = "Se ha eliminado el perfil del usuario.";
    public static String LOGOUT_SUCCESS = "Se ha cerrado la sesión correctamente.";
    public static String LOGOUT_FAILED = "Error: No se pudo cerrar sesión.";
    public static String ACCOUNT_DELETED = "La cuenta se ha eliminado correctamente.";
    public static String AUTHORITY_CHANGED = "Se han modificado los permisos del usuario.";
    public static String DELETING_ERROR = "No se ha podido eliminar el perfil del usuario.";
    public static String ERROR_LOADING_PROFILE = "Error cargando el perfil del usuario.";
}
