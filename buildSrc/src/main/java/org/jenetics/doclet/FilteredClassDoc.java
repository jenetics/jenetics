/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.doclet;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.sun.javadoc.AnnotatedType;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.SourcePosition;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;
import com.sun.javadoc.WildcardType;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-07-31 $</em>
 */
class FilteredClassDoc implements ClassDoc {

	private final ClassDoc _doc;
	private final Predicate<Type> _filter;

	FilteredClassDoc(final ClassDoc doc, final Predicate<Type> filter) {
		_doc = requireNonNull(doc);
		_filter = requireNonNull(filter);
	}

	@Override
	public boolean isAbstract() {
		return _doc.isAbstract();
	}

	@Override
	public boolean isSerializable() {
		return _doc.isSerializable();
	}

	@Override
	public boolean isExternalizable() {
		return _doc.isExternalizable();
	}

	@Override
	public MethodDoc[] serializationMethods() {
		return _doc.serializationMethods();
	}

	@Override
	public FieldDoc[] serializableFields() {
		return _doc.serializableFields();
	}

	@Override
	public boolean definesSerializableFields() {
		return _doc.definesSerializableFields();
	}

	@Override
	public ClassDoc superclass() {
		return Optional.ofNullable(_doc.superclass())
			.filter(_filter)
			.orElse(null);
	}

	@Override
	public Type superclassType() {
		return Optional.ofNullable(_doc.superclassType())
			.filter(_filter)
			.orElse(null);
	}

	@Override
	public boolean subclassOf(final ClassDoc classDoc) {
		return _doc.subclassOf(classDoc);
	}

	@Override
	public ClassDoc[] interfaces() {
		return Stream.of(_doc.interfaces())
			.filter(_filter)
			.toArray(ClassDoc[]::new);
	}

	@Override
	public Type[] interfaceTypes() {
		return _doc.interfaceTypes();
	}

	@Override
	public TypeVariable[] typeParameters() {
		return _doc.typeParameters();
	}

	@Override
	public ParamTag[] typeParamTags() {
		return _doc.typeParamTags();
	}

	@Override
	public FieldDoc[] fields() {
		return _doc.fields();
	}

	@Override
	public FieldDoc[] fields(final boolean b) {
		return _doc.fields(b);
	}

	@Override
	public FieldDoc[] enumConstants() {
		return _doc.enumConstants();
	}

	@Override
	public MethodDoc[] methods() {
		return _doc.methods();
	}

	@Override
	public MethodDoc[] methods(final boolean b) {
		return _doc.methods(b);
	}

	@Override
	public ConstructorDoc[] constructors() {
		return _doc.constructors();
	}

	@Override
	public ConstructorDoc[] constructors(final boolean b) {
		return _doc.constructors(b);
	}

	@Override
	public ClassDoc[] innerClasses() {
		return _doc.innerClasses();
	}

	@Override
	public ClassDoc[] innerClasses(final boolean b) {
		return _doc.innerClasses(b);
	}

	@Override
	public ClassDoc findClass(final String s) {
		return _doc.findClass(s);
	}

	@Override
	public ClassDoc[] importedClasses() {
		return _doc.importedClasses();
	}

	@Override
	public PackageDoc[] importedPackages() {
		return _doc.importedPackages();
	}

	@Override
	public ClassDoc containingClass() {
		return _doc.containingClass();
	}

	@Override
	public PackageDoc containingPackage() {
		return _doc.containingPackage();
	}

	@Override
	public String qualifiedName() {
		return _doc.qualifiedName();
	}

	@Override
	public int modifierSpecifier() {
		return _doc.modifierSpecifier();
	}

	@Override
	public String modifiers() {
		return _doc.modifiers();
	}

	@Override
	public AnnotationDesc[] annotations() {
		return _doc.annotations();
	}

	@Override
	public boolean isPublic() {
		return _doc.isPublic();
	}

	@Override
	public boolean isProtected() {
		return _doc.isProtected();
	}

	@Override
	public boolean isPrivate() {
		return _doc.isPrivate();
	}

	@Override
	public boolean isPackagePrivate() {
		return _doc.isPackagePrivate();
	}

	@Override
	public boolean isStatic() {
		return _doc.isStatic();
	}

	@Override
	public boolean isFinal() {
		return _doc.isFinal();
	}

	@Override
	public String commentText() {
		return _doc.commentText();
	}

	@Override
	public Tag[] tags() {
		return _doc.tags();
	}

	@Override
	public Tag[] tags(final String s) {
		return _doc.tags(s);
	}

	@Override
	public SeeTag[] seeTags() {
		return _doc.seeTags();
	}

	@Override
	public Tag[] inlineTags() {
		return _doc.inlineTags();
	}

	@Override
	public Tag[] firstSentenceTags() {
		return _doc.firstSentenceTags();
	}

	@Override
	public String getRawCommentText() {
		return _doc.getRawCommentText();
	}

	@Override
	public void setRawCommentText(final String s) {
		_doc.setRawCommentText(s);
	}

	@Override
	public String name() {
		return _doc.name();
	}

	@Override
	public int compareTo(final Object o) {
		final Object doc = o instanceof FilteredClassDoc ?
			((FilteredClassDoc)o)._doc : o;

		return _doc.compareTo(doc);
	}

	@Override
	public boolean isField() {
		return _doc.isField();
	}

	@Override
	public boolean isEnumConstant() {
		return _doc.isEnumConstant();
	}

	@Override
	public boolean isConstructor() {
		return _doc.isConstructor();
	}

	@Override
	public boolean isMethod() {
		return _doc.isMethod();
	}

	@Override
	public boolean isAnnotationTypeElement() {
		return _doc.isAnnotationTypeElement();
	}

	@Override
	public boolean isInterface() {
		return _doc.isInterface();
	}

	@Override
	public boolean isException() {
		return _doc.isException();
	}

	@Override
	public boolean isError() {
		return _doc.isError();
	}

	@Override
	public boolean isEnum() {
		return _doc.isEnum();
	}

	@Override
	public boolean isAnnotationType() {
		return _doc.isAnnotationType();
	}

	@Override
	public boolean isOrdinaryClass() {
		return _doc.isOrdinaryClass();
	}

	@Override
	public boolean isClass() {
		return _doc.isClass();
	}

	@Override
	public boolean isIncluded() {
		return _doc.isIncluded();
	}

	@Override
	public SourcePosition position() {
		return _doc.position();
	}

	@Override
	public String typeName() {
		return _doc.typeName();
	}

	@Override
	public String qualifiedTypeName() {
		return _doc.qualifiedTypeName();
	}

	@Override
	public String simpleTypeName() {
		return _doc.simpleTypeName();
	}

	@Override
	public String dimension() {
		return _doc.dimension();
	}

	@Override
	public boolean isPrimitive() {
		return _doc.isPrimitive();
	}

	@Override
	public ClassDoc asClassDoc() {
		return _doc.asClassDoc();
	}

	@Override
	public ParameterizedType asParameterizedType() {
		return _doc.asParameterizedType();
	}

	@Override
	public TypeVariable asTypeVariable() {
		return _doc.asTypeVariable();
	}

	@Override
	public WildcardType asWildcardType() {
		return _doc.asWildcardType();
	}

	@Override
	public AnnotatedType asAnnotatedType() {
		return _doc.asAnnotatedType();
	}

	@Override
	public AnnotationTypeDoc asAnnotationTypeDoc() {
		return _doc.asAnnotationTypeDoc();
	}

	@Override
	public Type getElementType() {
		return _doc.getElementType();
	}

}
