package cn.itruschina.crl.sharding;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;

/**
 * @Author: yang_yunxiang
 * @Date: 2019/5/7 12:13
 */
@Slf4j
public class InitialPreciseShardingAlgorithm implements PreciseShardingAlgorithm<String> {
    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<String> shardingValue) {
        log.info("availableTargetNames:" + JSON.toJSONString(availableTargetNames) + ",shardingValue:" + JSON.toJSONString(shardingValue));
        char start = shardingValue.getValue().charAt(0);
        long index;
        try {
            index = Long.parseLong(String.valueOf(start), 16);
        } catch (Exception e) {
            log.error("首字母分片解析失败", e);
            return null;
        }
        for (String name : availableTargetNames) {
            if (name.endsWith(String.valueOf(index))) {
                return name;
            }
        }
        return null;
    }
}