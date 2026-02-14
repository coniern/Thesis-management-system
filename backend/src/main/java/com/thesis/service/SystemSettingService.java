package com.thesis.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.thesis.entity.SystemSetting;
import com.thesis.mapper.SystemSettingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SystemSettingService {

    @Autowired
    private SystemSettingMapper settingMapper;

    public String getSettingValue(String settingKey) {
        LambdaQueryWrapper<SystemSetting> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemSetting::getSettingKey, settingKey);
        SystemSetting setting = settingMapper.selectOne(wrapper);
        return setting != null ? setting.getSettingValue() : null;
    }

    public void updateSetting(String settingKey, String settingValue, String description) {
        LambdaQueryWrapper<SystemSetting> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemSetting::getSettingKey, settingKey);

        SystemSetting existingSetting = settingMapper.selectOne(wrapper);

        if (existingSetting != null) {
            LambdaUpdateWrapper<SystemSetting> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(SystemSetting::getId, existingSetting.getId())
                    .set(SystemSetting::getSettingValue, settingValue)
                    .set(SystemSetting::getDescription, description)
                    .set(SystemSetting::getUpdatedAt, LocalDateTime.now());
            settingMapper.update(null, updateWrapper);
        } else {
            SystemSetting setting = new SystemSetting();
            setting.setSettingKey(settingKey);
            setting.setSettingValue(settingValue);
            setting.setDescription(description);
            setting.setCreatedAt(LocalDateTime.now());
            setting.setUpdatedAt(LocalDateTime.now());
            settingMapper.insert(setting);
        }
    }

    public List<SystemSetting> getAllSettings() {
        return settingMapper.selectList(null);
    }
}