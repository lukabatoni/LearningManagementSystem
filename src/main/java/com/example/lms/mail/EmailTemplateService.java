package com.example.lms.mail;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class EmailTemplateService {
  private final MustacheFactory mustacheFactory = new DefaultMustacheFactory();
  private static final String TEMPLATE_DIR = "templates/emails/";
  private static final String TEMPLATE_NOT_FOUND = "Template not found: ";
  private static final String REMINDER_TEMPLATE = "reminder_";
  private static final String MUSTACHE_EXTENSION = ".mustache";

  public String renderReminderTemplate(String locale, Map<String, Object> variables) {
    String templateName = buildReminderTemplateName(locale);
    var inputStream = getClass().getClassLoader().getResourceAsStream(templateName);
    if (inputStream == null) {
      throw new IllegalArgumentException(TEMPLATE_NOT_FOUND + templateName);
    }
    InputStreamReader reader = new InputStreamReader(inputStream);
    Mustache mustache = mustacheFactory.compile(reader, templateName);

    StringWriter writer = new StringWriter();
    mustache.execute(writer, variables);
    return writer.toString();
  }

  private String buildReminderTemplateName(String locale) {
    return TEMPLATE_DIR + REMINDER_TEMPLATE + locale.toLowerCase() + MUSTACHE_EXTENSION;
  }
}
