package com.peaceful.common.redis.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangjun on 14-8-18.
 */

public abstract class AbstractConfiguration implements AppConfigs {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractConfiguration.class);

    protected abstract Map<String, String> getConfigData();

    protected abstract String getStringFromStore(String key);

    @Override
    public Double getDouble(String key) {
        return getDouble(key, null);
    }

    @Override
    public final Double getDouble(String key, Double defaultValue) {
        String value = getString(key);
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            LOGGER.error("failed to convert {} to double, error is {}.", value, e);
            return defaultValue;
        }
    }

    @Override
    public Float getFloat(String key) {
        return getFloat(key, null);
    }

    @Override
    public Float getFloat(String key, Float defaultValue) {
        String value = getString(key);
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(value);
        } catch (Exception e) {
            LOGGER.error("failed to convert {} to float, error is {}.", value, e);
            return defaultValue;
        }
    }

    @Override
    public Integer getInt(String key) {
        return getInt(key, null);
    }

    @Override
    public Integer getInt(String key, Integer defaultValue) {
        String value = getString(key);
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            LOGGER.error("failed to convert {} to Int, error is {}.", value, e);
            return defaultValue;
        }
    }

    @Override
    public Long getLong(String key) {
        return getLong(key, null);
    }

    @Override
    public Long getLong(String key, Long defaultValue) {
        String value = getString(key);
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            LOGGER.error("failed to convert {} to Long, error is {}.", value, e);
            return defaultValue;
        }
    }

    @Override
    public String getString(String key) {
        return getString(key, null);
    }

    @Override
    public Boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    @Override
    public Boolean getBoolean(String key, boolean defaultValue) {
        String value = getString(key);
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }
        try {
            return Boolean.parseBoolean(value);
        } catch (Exception e) {
            LOGGER.error("failed to convert {} to Boolean, error is {}.", value, e);
            return defaultValue;
        }
    }

    @Override
    public String getString(String key, String defaultValue) {
        String value = getStringFromStore(key);
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }
        return value;
    }


    @Override
    public Map<String, String> toMap() {
        Map<String, String> result = new HashMap<String, String>(getConfigData());
        return Collections.unmodifiableMap(result);
    }


    @Override
    public String toString() {
        if (getConfigData() != null) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Configuration ::").append("\r\n");
            for (String key : getConfigData().keySet()) {
                stringBuffer.append("[")
                        .append(key).append("=").append(getConfigData().get(key))
                        .append("]")
                        .append("\r\n");
            }
            return stringBuffer.toString();
        } else {
            return "[]";
        }
    }
}