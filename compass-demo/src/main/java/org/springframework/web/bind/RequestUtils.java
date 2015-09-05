package org.springframework.web.bind;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.HttpRequestMethodNotSupportedException;

public abstract class RequestUtils {
	public static void rejectRequestMethod(HttpServletRequest request,
			String method) throws ServletException {
		if (request.getMethod().equals(method))
			throw new HttpRequestMethodNotSupportedException(method);
	}

	public static Integer getIntParameter(HttpServletRequest request,
			String name) throws ServletRequestBindingException {
		return ServletRequestUtils.getIntParameter(request, name);
	}

	public static int getIntParameter(HttpServletRequest request, String name,
			int defaultVal) {
		return ServletRequestUtils.getIntParameter(request, name, defaultVal);
	}

	public static int[] getIntParameters(HttpServletRequest request, String name) {
		return ServletRequestUtils.getIntParameters(request, name);
	}

	public static int getRequiredIntParameter(HttpServletRequest request,
			String name) throws ServletRequestBindingException {
		return ServletRequestUtils.getRequiredIntParameter(request, name);
	}

	public static int[] getRequiredIntParameters(HttpServletRequest request,
			String name) throws ServletRequestBindingException {
		return ServletRequestUtils.getRequiredIntParameters(request, name);
	}

	public static Long getLongParameter(HttpServletRequest request, String name)
			throws ServletRequestBindingException {
		return ServletRequestUtils.getLongParameter(request, name);
	}

	public static long getLongParameter(HttpServletRequest request,
			String name, long defaultVal) {
		return ServletRequestUtils.getLongParameter(request, name, defaultVal);
	}

	public static long[] getLongParameters(HttpServletRequest request,
			String name) {
		return ServletRequestUtils.getLongParameters(request, name);
	}

	public static long getRequiredLongParameter(HttpServletRequest request,
			String name) throws ServletRequestBindingException {
		return ServletRequestUtils.getRequiredLongParameter(request, name);
	}

	public static long[] getRequiredLongParameters(HttpServletRequest request,
			String name) throws ServletRequestBindingException {
		return ServletRequestUtils.getRequiredLongParameters(request, name);
	}

	public static Float getFloatParameter(HttpServletRequest request,
			String name) throws ServletRequestBindingException {
		return ServletRequestUtils.getFloatParameter(request, name);
	}

	public static float getFloatParameter(HttpServletRequest request,
			String name, float defaultVal) {
		return ServletRequestUtils.getFloatParameter(request, name, defaultVal);
	}

	public static float[] getFloatParameters(HttpServletRequest request,
			String name) {
		return ServletRequestUtils.getFloatParameters(request, name);
	}

	public static float getRequiredFloatParameter(HttpServletRequest request,
			String name) throws ServletRequestBindingException {
		return ServletRequestUtils.getRequiredFloatParameter(request, name);
	}

	public static float[] getRequiredFloatParameters(
			HttpServletRequest request, String name)
			throws ServletRequestBindingException {
		return ServletRequestUtils.getRequiredFloatParameters(request, name);
	}

	public static Double getDoubleParameter(HttpServletRequest request,
			String name) throws ServletRequestBindingException {
		return ServletRequestUtils.getDoubleParameter(request, name);
	}

	public static double getDoubleParameter(HttpServletRequest request,
			String name, double defaultVal) {
		return ServletRequestUtils
				.getDoubleParameter(request, name, defaultVal);
	}

	public static double[] getDoubleParameters(HttpServletRequest request,
			String name) {
		return ServletRequestUtils.getDoubleParameters(request, name);
	}

	public static double getRequiredDoubleParameter(HttpServletRequest request,
			String name) throws ServletRequestBindingException {
		return ServletRequestUtils.getRequiredDoubleParameter(request, name);
	}

	public static double[] getRequiredDoubleParameters(
			HttpServletRequest request, String name)
			throws ServletRequestBindingException {
		return ServletRequestUtils.getRequiredDoubleParameters(request, name);
	}

	public static Boolean getBooleanParameter(HttpServletRequest request,
			String name) throws ServletRequestBindingException {
		if (request.getParameter(name) == null) {
			return null;
		}
		return getRequiredBooleanParameter(request, name) ? Boolean.TRUE
				: Boolean.FALSE;
	}

	public static boolean getBooleanParameter(HttpServletRequest request,
			String name, boolean defaultVal) {
		if (request.getParameter(name) == null)
			return defaultVal;
		try {
			return getRequiredBooleanParameter(request, name);
		} catch (ServletRequestBindingException ex) {
		}
		return defaultVal;
	}

	public static boolean[] getBooleanParameters(HttpServletRequest request,
			String name) {
		try {
			return getRequiredBooleanParameters(request, name);
		} catch (ServletRequestBindingException ex) {
		}
		return new boolean[0];
	}

	public static boolean getRequiredBooleanParameter(
			HttpServletRequest request, String name)
			throws ServletRequestBindingException {
		boolean value = ServletRequestUtils.getRequiredBooleanParameter(
				request, name);
		if ((!value) && ("".equals(request.getParameter(name)))) {
			throw new ServletRequestBindingException(
					"Required boolean parameter '" + name
							+ "' contains no value");
		}

		return value;
	}

	public static boolean[] getRequiredBooleanParameters(
			HttpServletRequest request, String name)
			throws ServletRequestBindingException {
		boolean[] values = ServletRequestUtils.getRequiredBooleanParameters(
				request, name);
		for (int i = 0; i < values.length; i++) {
			if ((values[i] == false)
					&& ("".equals(request.getParameterValues(name)[i]))) {
				throw new ServletRequestBindingException(
						"Required boolean parameter '" + name
								+ "' contains no value");
			}
		}

		return values;
	}

	public static String getStringParameter(HttpServletRequest request,
			String name) throws ServletRequestBindingException {
		if (request.getParameter(name) == null) {
			return null;
		}
		return getRequiredStringParameter(request, name);
	}

	public static String getStringParameter(HttpServletRequest request,
			String name, String defaultVal) {
		if (request.getParameter(name) == null)
			return defaultVal;
		try {
			return getRequiredStringParameter(request, name);
		} catch (ServletRequestBindingException ex) {
		}
		return defaultVal;
	}

	public static String[] getStringParameters(HttpServletRequest request,
			String name) {
		try {
			return getRequiredStringParameters(request, name);
		} catch (ServletRequestBindingException ex) {
		}
		return new String[0];
	}

	public static String getRequiredStringParameter(HttpServletRequest request,
			String name) throws ServletRequestBindingException {
		String value = ServletRequestUtils.getRequiredStringParameter(request,
				name);
		if ("".equals(value)) {
			throw new ServletRequestBindingException(
					"Required string parameter '" + name
							+ "' contains no value");
		}

		return value;
	}

	public static String[] getRequiredStringParameters(
			HttpServletRequest request, String name)
			throws ServletRequestBindingException {
		String[] values = ServletRequestUtils.getRequiredStringParameters(
				request, name);
		for (int i = 0; i < values.length; i++) {
			if ("".equals(values[i])) {
				throw new ServletRequestBindingException(
						"Required string parameter '" + name
								+ "' contains no value");
			}
		}

		return values;
	}
}