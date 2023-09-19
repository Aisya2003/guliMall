package com.example.mall.seckill.schedule;

import com.example.mall.common.model.constant.RedisConstant;
import com.example.mall.seckill.config.ScheduleTaskConfig;
import com.example.mall.seckill.service.SeckillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Import({
        ScheduleTaskConfig.class
})
public class ScheduleTaskService {
    private final SeckillService seckillService;
    private final RedissonClient redissonClient;

    @Scheduled(cron = "0 * * * * ?")
    public void uploadSeckillSkuLast3Day() {
        RLock lock = redissonClient.getLock(RedisConstant.Seckill.SECKILL_LOCK);
        try {
            lock.lock();
            log.info("上传秒杀商品");
            seckillService.uploadProduct();
        } finally {
            lock.unlock();
        }
    }
}
