package org.blazer.dataservice.util;

import java.util.HashSet;

public class ParamsUtil {

	public static final HashSet<String> Set = new HashSet<String>() {
		private static final long serialVersionUID = -2212280133747455803L;
		{
			add("${result_path}");
			add("${mapping_config_job_id}");
			add("${config_id}");
			add("${emails}");
		}
	};

}
