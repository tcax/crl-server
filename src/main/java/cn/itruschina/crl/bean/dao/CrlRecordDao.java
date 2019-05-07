package cn.itruschina.crl.bean.dao;

import cn.itruschina.crl.bean.domain.CrlRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: yang_yunxiang
 * @Date: 2019/4/17 13:30
 */

@Repository
public interface CrlRecordDao extends JpaRepository<CrlRecord, Long> {

    /***
     * 根据CA配置ID和证书序列号查询证书吊销记录
     * @param caCongigId
     * @param serialNumber
     * @return
     */
    CrlRecord findByCaConfigIdAndSerialNumber(long caCongigId, String serialNumber);

    /***
     * 根据CA配置获取证书吊销记录列表
     * @param caCongigId
     * @return
     */
    List<CrlRecord> findByCaConfigId(long caCongigId);
}
