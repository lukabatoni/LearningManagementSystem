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

  private static final String TEMPLATE_DIR = "templates/emails/";

  public String renderReminderTemplate(String locale, Map<String, Object> variables) {
    MustacheFactory mf = new DefaultMustacheFactory();
    String templateName = TEMPLATE_DIR + "reminder_" + locale.toLowerCase() + ".mustache";
    var inputStream = getClass().getClassLoader().getResourceAsStream(templateName);
    if (inputStream == null) {
      throw new IllegalArgumentException("Template not found: " + templateName);
    }
    InputStreamReader reader = new InputStreamReader(inputStream);
    Mustache mustache = mf.compile(reader, templateName);

    StringWriter writer = new StringWriter();
    mustache.execute(writer, variables);
    return writer.toString();
  }
}
