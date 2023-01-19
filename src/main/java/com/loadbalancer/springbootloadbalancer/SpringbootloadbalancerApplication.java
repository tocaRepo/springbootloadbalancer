package com.loadbalancer.springbootloadbalancer;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@SpringBootApplication
@RestController
public class SpringbootloadbalancerApplication {
	@Autowired
	private HttpServletRequest request;

	private final WebClient loadBalancedWebClientBuilder;

	public SpringbootloadbalancerApplication(WebClient.Builder webClientBuilder) {
		final int size = 16 * 1024 * 1024;
		final ExchangeStrategies strategies = ExchangeStrategies.builder()
				.codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
				.build();

		this.loadBalancedWebClientBuilder = webClientBuilder.exchangeStrategies(strategies)
				.build();

	}

	public static void main(String[] args) {
		SpringApplication.run(SpringbootloadbalancerApplication.class, args);
	}


	@RequestMapping(value = {"/**"}, method = RequestMethod.GET)
	public Mono<String> get(@RequestHeader HttpHeaders headers) {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = requestAttributes.getRequest();
		String path = request.getRequestURI();
		String queryString = request.getQueryString();
		return loadBalancedWebClientBuilder.get().uri("http://localhost:444"+path+"?"+queryString)
				.headers(httpHeaders -> httpHeaders.addAll(headers)).retrieve().bodyToMono(String.class);

	}

	@RequestMapping(value = {"**"}, method = RequestMethod.POST)
	public Mono<String> post(@RequestHeader HttpHeaders headers,@RequestBody String raw) {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = requestAttributes.getRequest();
		String path = request.getRequestURI();

		return loadBalancedWebClientBuilder.post().uri("http://localhost:444"+path).bodyValue(raw)
				.headers(httpHeaders -> httpHeaders.addAll(headers)).retrieve().bodyToMono(String.class);

	}



}
