package org.reflections.scanners;

import static org.reflections.util.Utils.join;

import java.util.Objects;
import javassist.CtBehavior;
import javassist.NotFoundException;
import javassist.bytecode.MethodInfo;
import javassist.expr.ConstructorCall;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;
import javassist.expr.NewExpr;
import org.reflections.MemberInfo;
import org.reflections.adapters.MetadataAdapter;

public class MemberInfoFactory {

    private final MetadataAdapter metadataAdapter;

    public MemberInfoFactory(MetadataAdapter metadataAdapter) {
        this.metadataAdapter = Objects.requireNonNull(metadataAdapter);
    }

    public MemberInfo createMemberInfo(CtBehavior member) {
        return MemberInfo.builder()
            .source(member)
            .className(member.getDeclaringClass().getName())
            .refName(member.getMethodInfo().getName())
            .params(parameterNames(member.getMethodInfo()))
            .lineNumber(member.getMethodInfo().getLineNumber(0))
            .build();
    }

    public MemberInfo createMemberInfo(NewExpr e) throws NotFoundException {
        return MemberInfo.builder()
            .source(e)
            .className(e.getConstructor().getDeclaringClass().getName())
            .refName("<init>")
            .params(parameterNames(e.getConstructor().getMethodInfo()))
            .lineNumber(e.getLineNumber())
            .build();
    }

    public MemberInfo createMemberInfo(MethodCall m) throws NotFoundException {
        return MemberInfo.builder()
            .source(m)
            .className(m.getMethod().getDeclaringClass().getName())
            .refName(m.getMethodName())
            .params(parameterNames(m.getMethod().getMethodInfo()))
            .lineNumber(m.getLineNumber())
            .build();
    }

    public MemberInfo createMemberInfo(ConstructorCall c) throws NotFoundException {
        return MemberInfo.builder()
            .source(c)
            .className(c.getConstructor().getDeclaringClass().getName())
            .refName("<init>")
            .params(parameterNames(c.getConstructor().getMethodInfo()))
            .lineNumber(c.getLineNumber())
            .build();
    }

    public MemberInfo createMemberInfo(FieldAccess f) throws NotFoundException {
        return MemberInfo.builder()
            .source(f)
            .className(f.getField().getDeclaringClass().getName())
            .refName(f.getFieldName())
            .lineNumber(f.getLineNumber())
            .build();
    }

    private String parameterNames(MethodInfo info) {
        return join(metadataAdapter.getParameterNames(info), ", ");
    }
}
