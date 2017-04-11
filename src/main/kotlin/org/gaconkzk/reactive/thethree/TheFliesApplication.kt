package org.gaconkzk.reactive.thethree

import com.samskivert.mustache.Mustache
import org.gaconkzk.reactive.thethree.util.MarkdownConverter
import org.gaconkzk.reactive.thethree.util.run
import org.gaconkzk.reactive.thethree.web.MixitWebFilter
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.BeanFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.domain.EntityScanner
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.data.annotation.Persistent
import org.springframework.data.mapping.model.FieldNamingStrategy
import org.springframework.data.mongodb.core.convert.CustomConversions
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoMappingContext
import org.springframework.web.reactive.result.view.mustache.MustacheResourceTemplateLoader
import org.springframework.web.reactive.result.view.mustache.MustacheViewResolver

@SpringBootApplication
@EnableConfigurationProperties(TheFliesProperties::class)
class TheFliesApplication {

    @Bean
    fun viewResolver(messageSource: MessageSource, properties: TheFliesProperties) = MustacheViewResolver().apply {
        val prefix = "classpath:/templates/"
        val suffix = ".mustache"
        val loader = MustacheResourceTemplateLoader(prefix, suffix)
        setPrefix(prefix)
        setSuffix(suffix)
        setCompiler(Mustache.compiler().escapeHTML(false).withLoader(loader))
    }

    @Bean
    fun filter(properties: TheFliesProperties) = MixitWebFilter(properties)

    @Bean
    fun markdownConverter() = MarkdownConverter()

    /**
     * Just for fixing SPR-15429 https://jira.spring.io/browse/SPR-15429 - This will be removed
     * soon.
     */
    @Bean
    @ConditionalOnMissingBean
    @Throws(ClassNotFoundException::class)
    fun mongoMappingContext(beanFactory: BeanFactory,
                            conversions: CustomConversions,
                            applicationContext: ApplicationContext,
                            properties: MongoProperties): MongoMappingContext {
        val context = MongoMappingContext()
        context.setInitialEntitySet(EntityScanner(applicationContext)
                .scan(Document::class.java, Persistent::class.java))
        val strategyClass = properties.getFieldNamingStrategy()
        if (strategyClass != null) {
            context.setFieldNamingStrategy(
                    BeanUtils.instantiateClass(strategyClass) as FieldNamingStrategy)
        }
        context.setSimpleTypeHolder(conversions.simpleTypeHolder)
        return context
    }

}

fun main(args: Array<String>) {
    run(TheFliesApplication::class, *args)
}
