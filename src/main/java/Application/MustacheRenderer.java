package Application;

import java.io.IOException;
import java.io.StringWriter;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

public class MustacheRenderer {
    private static final String TEMPLATE_ROOT = "templates";

    private MustacheFactory mustacheFactory;


    public MustacheRenderer() {
        this(TEMPLATE_ROOT);
    }


    public MustacheRenderer(String templateRoot) {
        mustacheFactory = new DefaultMustacheFactory(templateRoot);
    }

    public String render(String templateName, Object model) {
        Mustache mustache = mustacheFactory.compile(templateName);
        try (StringWriter stringWriter = new StringWriter()) {
            mustache.execute(stringWriter, model).close();
            return stringWriter.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
