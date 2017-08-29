package org.iotp.infomgt.dao.sql.settings;

import org.iotp.infomgt.dao.DaoUtil;
import org.iotp.infomgt.dao.model.sql.AdminSettingsEntity;
import org.iotp.infomgt.dao.settings.AdminSettingsDao;
import org.iotp.infomgt.dao.sql.JpaAbstractDao;
import org.iotp.infomgt.dao.util.SqlDao;
import org.iotp.infomgt.data.AdminSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@SqlDao
public class JpaAdminSettingsDao extends JpaAbstractDao<AdminSettingsEntity, AdminSettings> implements AdminSettingsDao {

    @Autowired
    private AdminSettingsRepository adminSettingsRepository;

    @Override
    protected Class<AdminSettingsEntity> getEntityClass() {
        return AdminSettingsEntity.class;
    }

    @Override
    protected CrudRepository<AdminSettingsEntity, String> getCrudRepository() {
        return adminSettingsRepository;
    }

    @Override
    public AdminSettings findByKey(String key) {
        return DaoUtil.getData(adminSettingsRepository.findByKey(key));
    }
}
