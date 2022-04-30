package com.womakerscode.microservicemeetups;

import com.womakerscode.microservicemeetups.util.DateUtil;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class ServicoDeAgendamentoDeMeetupsApplication {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(localDateTimeToStringConverter());
        modelMapper.addConverter(stringToLocalDateTimeConverter());
        return modelMapper;
    }

    public static void main(String[] args) {
        SpringApplication.run(ServicoDeAgendamentoDeMeetupsApplication.class, args);
    }

    private Converter<String, LocalDateTime> localDateTimeToStringConverter() {
        return new AbstractConverter<String, LocalDateTime>() {
            @Override
            protected LocalDateTime convert(String source) {
                return DateUtil.convertStringToLocalDateTimeWithTime(source);
            }
        };
    }

    private Converter<LocalDateTime, String> stringToLocalDateTimeConverter() {
        return new AbstractConverter<LocalDateTime, String>() {
            @Override
            protected String convert(LocalDateTime source) {
                return DateUtil.formatLocalDateTimeToStringWithTime(source);
            }
        };
    }


}
