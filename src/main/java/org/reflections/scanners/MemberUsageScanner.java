package org.reflections.scanners;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import javassist.expr.ConstructorCall;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;
import javassist.expr.NewExpr;
import org.reflections.MemberInfo;
import org.reflections.ReflectionsException;
import org.reflections.Store;
import org.reflections.util.ClasspathHelper;

/** scans methods/constructors/fields usage
 * <p><i> depends on {@link org.reflections.adapters.JavassistAdapter} configured </i>*/
@SuppressWarnings("unchecked")
public class MemberUsageScanner extends AbstractScanner {
    private ClassPool classPool;

    @Override
    public void scan(Object cls, Store store) {
        MemberInfoFactory memberFactory = new MemberInfoFactory(getMetadataAdapter());

        try {
            CtClass ctClass = getClassPool().get(getMetadataAdapter().getClassName(cls));
            for (CtBehavior member : ctClass.getDeclaredConstructors()) {
                scanMember(member, store, memberFactory);
            }
            for (CtBehavior member : ctClass.getDeclaredMethods()) {
                scanMember(member, store, memberFactory);
            }
            ctClass.detach();
        } catch (Exception e) {
            throw new ReflectionsException("Could not scan method usage for " + getMetadataAdapter().getClassName(cls), e);
        }
    }

    void scanMember(CtBehavior member, Store store, MemberInfoFactory memberFactory) throws CannotCompileException {
        //key contains this$/val$ means local field/parameter closure
        final MemberInfo valueMember = memberFactory.createMemberInfo(member);

        member.instrument(new ExprEditor() {
            @Override
            public void edit(NewExpr e) throws CannotCompileException {
                try {
                    put(store, memberFactory.createMemberInfo(e), valueMember);
                } catch (NotFoundException e1) {
                    throw new ReflectionsException("Could not find new instance usage in " + valueMember, e1);
                }
            }

            @Override
            public void edit(MethodCall m) throws CannotCompileException {
                try {
                    put(store, memberFactory.createMemberInfo(m), valueMember);
                } catch (NotFoundException e) {
                    throw new ReflectionsException("Could not find member " + m.getClassName() + " in " + valueMember,
                        e);
                }
            }

            @Override
            public void edit(ConstructorCall c) throws CannotCompileException {
                try {
                    put(store, memberFactory.createMemberInfo(c), valueMember);
                } catch (NotFoundException e) {
                    throw new ReflectionsException("Could not find member " + c.getClassName() + " in " + valueMember,
                        e);
                }
            }

            @Override
            public void edit(FieldAccess f) throws CannotCompileException {
                try {
                    put(store, memberFactory.createMemberInfo(f), valueMember);
                } catch (NotFoundException e) {
                    throw new ReflectionsException("Could not find member " + f.getFieldName() + " in " + valueMember,
                        e);
                }
            }
        });
    }

    private ClassPool getClassPool() {
        if (classPool == null) {
            synchronized (this) {
                classPool = new ClassPool();
                ClassLoader[] classLoaders = getConfiguration().getClassLoaders();
                if (classLoaders == null) {
                    classLoaders = ClasspathHelper.classLoaders();
                }
                for (ClassLoader classLoader : classLoaders) {
                    classPool.appendClassPath(new LoaderClassPath(classLoader));
                }
            }
        }
        return classPool;
    }
}
