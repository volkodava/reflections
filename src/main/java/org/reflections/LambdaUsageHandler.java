package org.reflections;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LambdaUsageHandler {

    private static final String LAMBDA_REF = ".lambda$";
    private static final Pattern LAMBDA_REF_PATTERN = Pattern.compile("^(.*?)\\.lambda\\$(.*?)\\$(\\d+)");

    public boolean isLambda(String name) {
        return name.contains(LAMBDA_REF);
    }

    public String findRefSubstitution(String lambdaRefValue, Class<?> scannerClass, Store store) {
        // give lambda ref second chance
        String className = className(lambdaRefValue);
        String methodName = methodName(lambdaRefValue);
        int methodIndex = methodIndex(lambdaRefValue);
        String fullName = String.format("%s.%s", className, methodName);
        List<String> filteredValues = store.findValuesByFilter(scannerClass,
            key -> key.contains(className)).stream()
            .filter(key -> key.startsWith(fullName))
            .collect(Collectors.toList());

        if (methodIndex >= filteredValues.size()) {
            // no luck :,(
            return lambdaRefValue;
        }

        return filteredValues.get(methodIndex);
    }

    private String className(String lambdaRefValue) {
        Matcher versionMatcher = LAMBDA_REF_PATTERN.matcher(lambdaRefValue);
        if (!versionMatcher.find()) {
            throw new IllegalStateException(String.format("Can't parse %s", lambdaRefValue));
        }

        return versionMatcher.group(1).trim();
    }

    private String methodName(String lambdaRefValue) {
        Matcher versionMatcher = LAMBDA_REF_PATTERN.matcher(lambdaRefValue);
        if (!versionMatcher.find()) {
            return "";
        }

        return versionMatcher.group(2).trim();
    }

    private int methodIndex(String lambdaRefValue) {
        Matcher versionMatcher = LAMBDA_REF_PATTERN.matcher(lambdaRefValue);
        if (!versionMatcher.find()) {
            return 0;
        }

        return Integer.parseInt(versionMatcher.group(3).trim());
    }
}
