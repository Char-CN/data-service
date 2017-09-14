package org.blazer.dataservice.util;

import java.util.HashSet;

/**
 * 参数工具
 * 
 * @author hyy
 *
 */
public class ParamsUtil {

	/**
	 * 该Set是系统保留参数，当任务执行时候，还会传入参数SYS_TASK_NAME。由于SYS_TASK_NAME是系统允许的参数，不在该列表内。
	 */
	private static final HashSet<String> reservedMap = new HashSet<String>() {
		private static final long serialVersionUID = -2212280133747455803L;
		{
			add("${result_path}");
			add("${mapping_config_job_id}");
			add("${config_id}");
			add("${emails}");
		}
	};

	public static boolean isSysReserved(String param) {
		return reservedMap.contains(param);
	}

	/**
	 * 参数是否是Excel类型的
	 * 
	 * @return
	 */
	public static boolean isExcel(String param) {
		if (param != null && !"".equals(param) && !param.toLowerCase().startsWith("${excel.")) {
			return false;
		}
		return true;
	}

	/**
	 * 参数是否是Excel并且合法，配合isExcel一起使用。
	 * 
	 * @param param
	 * @return
	 */
	public static boolean isExcelValid(String param) {
		return param.length() > 9;
	}

}
