package cn.itruschina.crl.bean.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Author: yang_yunxiang
 * @Date: 2019/4/16 11:22
 */

@Entity
@Table(name = "authorization")
@Data
public class Authorization extends BaseEntity {

    @Column(unique = true)
    private String accessToken;

}
