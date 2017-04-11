package org.gaconkzk.reactive.thethree

import com.samskivert.mustache.Mustache
import org.gaconkzk.reactive.thethree.util.MarkdownConverter
import org.gaconkzk.reactive.thethree.util.run
import org.gaconkzk.reactive.thethree.web.TheFliesWebFilter
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
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
    fun filter(properties: TheFliesProperties) = TheFliesWebFilter(properties)

    @Bean
    fun markdownConverter() = MarkdownConverter()
}

fun main(args: Array<String>) {
    run(TheFliesApplication::class, *args)
}
