package org.reflections;

import java.util.Objects;

public class MemberInfo {

    private final Object source;
    private final String className;
    private final String refName;
    private final String params;
    private final int lineNumber;

    private MemberInfo(Object source, String className, String refName, String params, int lineNumber) {
        this.source = Objects.requireNonNull(source);
        this.className = Objects.requireNonNull(className);
        this.refName = Objects.requireNonNull(refName);
        this.params = Objects.requireNonNull(params);
        this.lineNumber = lineNumber;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static MemberInfo ofString(String className) {
        return new MemberInfo(null, className, "", "", 0);
    }

    public String getClassName() {
        return className;
    }

    public String getRefName() {
        return refName;
    }

    public String getParams() {
        return params;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public static final class Builder {

        private Object source;
        private String className;
        private String refName = "";
        private String params = "";
        private int lineNumber = 0;

        private Builder() {
        }

        public Builder source(Object source) {
            this.source = source;
            return this;
        }

        public Builder className(String className) {
            this.className = className;
            return this;
        }

        public Builder refName(String refName) {
            this.refName = refName;
            return this;
        }

        public Builder params(String params) {
            this.params = params;
            return this;
        }

        public Builder lineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
            return this;
        }

        public MemberInfo build() {
            return new MemberInfo(source, className, refName, params, lineNumber);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MemberInfo that = (MemberInfo) o;
        return Objects.equals(className, that.className) &&
            Objects.equals(refName, that.refName) &&
            Objects.equals(params, that.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, refName, params);
    }

    @Override
    public String toString() {
        return String.format("%s.%s(%s) #%s", className, refName, params, lineNumber);
    }
}
