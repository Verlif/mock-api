package idea.verlif.mockapi.arg;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Import;

@ConditionalOnProperty(prefix = "mockapi.arg", name = "enabled", matchIfMissing = true)
@Import({MockArgInitializer.class, ArgMockerCollector.class, MockArgResolver.class})
public class MockArgConfig {

    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
