package cn.itruschina.crl.sharding;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.math.BigInteger;
import java.util.Collection;

/**
 * @Author: yang_yunxiang
 * @Date: 2019/5/7 12:13
 */
@Slf4j
public class BigintPreciseShardingAlgorithm implements PreciseShardingAlgorithm<String> {
    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<String> shardingValue) {
        log.info("availableTargetNames:" + JSON.toJSONString(availableTargetNames) + ",shardingValue:" + JSON.toJSONString(shardingValue));
        BigInteger value = new BigInteger(shardingValue.getValue(), 16);
        BigInteger size = BigInteger.valueOf(availableTargetNames.size());
        try {
            for (String name : availableTargetNames) {
                if (name.endsWith(value.mod(size).toString())) {
                    return name;
                }
            }
        } catch (Exception e) {
            log.error("BigInteger分片解析失败", e);
            return null;
        }
        return null;
    }
}