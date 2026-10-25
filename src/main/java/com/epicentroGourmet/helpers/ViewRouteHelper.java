package com.epicentroGourmet.helpers;

public class ViewRouteHelper {
	/**** Views ****/
	//HOME
	public static final String INICIO = "home/inicio";
	public final static String INDEX = "home/index";
	public final static String HELLO = "home/hello";

	//USER
	public final static String USER_LOGIN = "user/login";
	public final static String USER_LOGOUT = "user/logout";

	// ADMIN
	public final static String ADMIN_FESTIVALES = "admin/festivales";

	// RESPONSABLE
	public final static String RESPONSABLE_EMPLEADOS = "responsable/empleados";
	public final static String RESPONSABLE_INICIO = "responsable/inicio";

	// EMPLEADO
	public final static String EMPLEADO_PERFIL = "empleado/perfil";

	/**** Redirects ****/
	public final static String ROUTE = "/index";
	public final static String ROUTE_ADMIN_FESTIVALES = "redirect:/admin/festivales";
	public final static String ROUTE_RESPONSABLE_EMPLEADOS = "redirect:/responsable/empleados";
	public final static String ROUTE_EMPLEADO_PERFIL = "redirect:/empleado/mi-informacion";

}
