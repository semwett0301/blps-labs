package com.example.code.config;

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.TransactionManagerServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.transaction.TransactionManager;

@Configuration
public class BitronixConfiguration {
    @Bean(name = "bitronixTransactionManager")
    public BitronixTransactionManager bitronixTransactionManager() throws Throwable {
        bitronix.tm.Configuration bitronixConfiguration = TransactionManagerServices.getConfiguration();

        BitronixTransactionManager bitronixTransactionManager = TransactionManagerServices.getTransactionManager();
        bitronixTransactionManager.setTransactionTimeout(1000);
        return bitronixTransactionManager;
    }

    @Bean(name = "transactionManager")
    @DependsOn({"bitronixTransactionManager"})
    public PlatformTransactionManager transactionManager(TransactionManager bitronixTransactionManager) {
        return new JtaTransactionManager(bitronixTransactionManager);
    }
}

