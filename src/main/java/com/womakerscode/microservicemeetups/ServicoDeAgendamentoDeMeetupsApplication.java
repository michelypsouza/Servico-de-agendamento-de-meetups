package com.womakerscode.microservicemeetups;

import com.womakerscode.microservicemeetups.util.DateUtil;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
public class ServicoDeAgendamentoDeMeetupsApplication {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(localDateToStringConverter());
        modelMapper.addConverter(stringToLocalDateConverter());
        return modelMapper;
    }

    private Converter<String, LocalDate> localDateToStringConverter() {
        Converter<String, LocalDate> varStringtoLocalDate = new AbstractConverter<String, LocalDate>() {
            @Override
            protected LocalDate convert(String source) {
                return DateUtil.convertStringToLocalDate(source);
            }
        };
        return varStringtoLocalDate;
    }

    private Converter<LocalDate, String> stringToLocalDateConverter() {
        Converter<LocalDate, String> varLocalDateToString = new AbstractConverter<LocalDate, String>() {
            @Override
            protected String convert(LocalDate source) {
                return DateUtil.formatLocalDateToString(source);
            }
        };
        return varLocalDateToString;
    }

    public static void main(String[] args) {
        SpringApplication.run(ServicoDeAgendamentoDeMeetupsApplication.class, args);
    }

}
