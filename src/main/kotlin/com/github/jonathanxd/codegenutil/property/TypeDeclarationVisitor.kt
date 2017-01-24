/**
 *      CodeGenUtil - Code generation utilities built on top of CodeAPI
 *
 *         The MIT License (MIT)
 *
 *      Copyright (c) 2017 JonathanxD <https://github.com/JonathanxD/>
 *      Copyright (c) contributors
 *
 *
 *      Permission is hereby granted, free of charge, to any person obtaining a copy
 *      of this software and associated documentation files (the "Software"), to deal
 *      in the Software without restriction, including without limitation the rights
 *      to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *      copies of the Software, and to permit persons to whom the Software is
 *      furnished to do so, subject to the following conditions:
 *
 *      The above copyright notice and this permission notice shall be included in
 *      all copies or substantial portions of the Software.
 *
 *      THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *      IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *      FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *      AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *      LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *      OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *      THE SOFTWARE.
 */
package com.github.jonathanxd.codegenutil.property

import com.github.jonathanxd.codeapi.CodeAPI
import com.github.jonathanxd.codeapi.MutableCodeSource
import com.github.jonathanxd.codeapi.base.TypeDeclaration
import com.github.jonathanxd.codeapi.builder.ConstructorDeclarationBuilder
import com.github.jonathanxd.codeapi.common.CodeModifier
import com.github.jonathanxd.codeapi.common.Data
import com.github.jonathanxd.codeapi.factory.field
import com.github.jonathanxd.codeapi.modify.visit.PartVisitor
import com.github.jonathanxd.codeapi.modify.visit.VisitManager
import java.util.*

class TypeDeclarationVisitor(val properties: Array<out Property>) : PartVisitor<TypeDeclaration> {

    override fun visit(codePart: TypeDeclaration, data: Data, visitManager: VisitManager<*>): TypeDeclaration {
        val body = codePart.body.toMutable()

        val fields = this.properties.map { field(EnumSet.of(CodeModifier.PRIVATE, CodeModifier.FINAL), it.type, it.name) }

        val constructor = ConstructorDeclarationBuilder.builder()
                .withModifiers(CodeModifier.PUBLIC)
                .withParameters(this.properties.map { CodeAPI.parameter(it.type, it.name) })
                .withBody(MutableCodeSource(
                        this.properties.map { CodeAPI.setThisField(it.type, it.name, CodeAPI.accessLocalVariable(it.type, it.name)) }
                ))
                .build()

        body.addAll(fields)

        body.add(constructor)

        return codePart.builder().withBody(visitManager.visit(body, data)).build()
    }
}