package com._hateam;

import com._hateam.common.config.CommonApplication;
import org.springframework.boot.SpringApplication;

@CommonApplication  // 공통 설정이 모두 적용됩니다.
public class HubApplication {
	public static void main(String[] args) {
		SpringApplication.run(HubApplication.class, args);
	}
}