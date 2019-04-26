package cn.itruschina.crl.bean.dao;

import cn.itruschina.crl.bean.domain.CaConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: yang_yunxiang
 * @Date: 2019/4/17 13:30
 */

@Repository
public interface CaConfigDao extends JpaRepository<CaConfig, Long> {

    /***
     * 根据证书主题获取CA配置
     * @param subjectDn
     * @return
     */
    List<CaConfig> findBySubjectDn(String subjectDn);

    /***
     * 根据全量CRL地址获取CA配置
     * @param baseCrlUrl
     * @return
     */
    List<CaConfig> findByBaseCrlUrl(String baseCrlUrl);

    /***
     * 根据增量CRL地址获取CA配置
     * @param deltaCrlUrl
     * @return
     */
    List<CaConfig> findByDeltaCrlUrl(String deltaCrlUrl);

    /***
     * 根据路径匹配全量或增量地址符合的CA配置
     * @param crlUrl 全量或增量CRL地址
     * @return
     */
    @Query(value = "from CaConfig cc where cc.baseCrlUrl = :crlUrl or cc.deltaCrlUrl = :crlUrl")
    CaConfig findByBaseCrlUrlOrDeltaCrlUrl(@Param("crlUrl") String crlUrl);

}
