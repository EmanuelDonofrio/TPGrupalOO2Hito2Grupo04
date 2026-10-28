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
	public final static String RESPONSABLE_EMPLEADOS_DESHABILITADOS = "responsable/empleadosDeshabilitados";
	public final static String RESPONSABLE_INICIO = "responsable/inicio";
	public final static String RESPONSABLE_PLATOS = "responsable/platos";
	public final static String RESPONSABLE_CALCULOS = "responsable/calculosyreportes";

	// EMPLEADO
	public final static String EMPLEADO_PERFIL = "empleado/perfil";
	public final static String EMPLEADO_NUEVO = "empleado/nuevo";

	// PLATO
	public final static String PLATO_NUEVO = "plato/nuevo";

	/**** Redirects ****/
	public final static String ROUTE = "/index";
	public final static String ROUTE_ADMIN_FESTIVALES = "redirect:/admin/festivales";
	public final static String ROUTE_RESPONSABLE_EMPLEADOS = "redirect:/responsable/empleados";
	public final static String ROUTE_RESPONSABLE_EMPLEADOS_DESHABILITADOS =
			"redirect:/responsable/empleadosDeshabilitados";
	public final static String ROUTE_EMPLEADO_PERFIL = "redirect:/empleado/mi-informacion";
	public final static String ROUTE_RESPONSABLE_PLATOS = "redirect:/responsable/platos";
	public final static String ROUTE_RESPONSABLE_CALCULOS = "redirect:/responsable/calculosyreportes";

}
