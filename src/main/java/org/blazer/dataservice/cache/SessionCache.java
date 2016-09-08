package org.blazer.dataservice.cache;

import java.util.UUID;

import org.blazer.dataservice.model.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component(value = "sessionCache")
public class SessionCache extends BaseCache {

	private static Logger logger = LoggerFactory.getLogger(SessionCache.class);

	public String add(UserModel userModel) {
		String sessionId = UUID.randomUUID().toString().replace("-", "");
		getCache().put(sessionId, userModel);
		logger.debug("add session id : " + sessionId + " user : " + userModel);
		return sessionId;
	}

	public UserModel get(String sessionId) {
		if (!contains(sessionId)) {
			return null;
		}
		return (UserModel) getCache().get(sessionId).get();
	}

	public boolean contains(String sessionId) {
		return getCache().get(sessionId) != null;
	}

	@Override
	public String getCacheName() {
		return "session_cache";
	}

}
