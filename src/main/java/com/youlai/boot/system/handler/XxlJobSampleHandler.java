package com.youlai.boot.system.handler;

import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * xxl-job 测试示例（Bean模式）
 */
@Component
@Slf4j
public class XxlJobSampleHandler {

    @XxlJob("demoJobHandler")
    public void demoJobHandler() {
        log.info("XXL-JOB, Hello World.");
    }

}
