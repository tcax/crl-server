package cn.itruschina.crl.bean.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @Author: yang_yunxiang
 * @Date: 2019/4/16 11:22
 */

@Entity
@Table(name = "auth_and_ca", uniqueConstraints = {@UniqueConstraint(columnNames = {"ca_config_id", "authorization_id"})})
@Data
public class AuthAndCa extends BaseEntity {

    @Column(name = "ca_config_id", nullable = false)
    private long caConfigId;
    @Column(name = "authorization_id", nullable = false)
    private long authorizationId;

    public AuthAndCa() {
    }

    public AuthAndCa(long caConfigId, long authorizationId) {
        this.caConfigId = caConfigId;
        this.authorizationId = authorizationId;
    }

}
