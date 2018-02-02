/**
 *      KoresGenUtil - Code generation utilities built on top of Kores
 *
 *         The MIT License (MIT)
 *
 *      Copyright (c) 2018 JonathanxD <https://github.com/JonathanxD/KoresGenUtil>
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
package com.github.jonathanxd.koresgenutil.property

import com.github.jonathanxd.iutils.data.TypedData
import com.github.jonathanxd.kores.base.ElementsHolder
import com.github.jonathanxd.kores.base.KoresModifier
import com.github.jonathanxd.kores.base.TypeDeclaration
import com.github.jonathanxd.kores.factory.fieldDec
import com.github.jonathanxd.kores.modify.visit.PartVisitor
import com.github.jonathanxd.kores.modify.visit.VisitManager

class TypeDeclarationVisitor(val properties: Array<out Property>) : PartVisitor<TypeDeclaration> {

    override fun visit(
        koresPart: TypeDeclaration,
        data: TypedData,
        visitManager: VisitManager<*>
    ): TypeDeclaration {
        return koresPart.builder()
            .fields(koresPart.fields + this.properties.map {
                fieldDec()
                    .modifiers(KoresModifier.PRIVATE, KoresModifier.FINAL)
                    .type(it.type)
                    .name(it.name)
                    .build()
            })
            .build().let {
            visitManager.visit(ElementsHolder::class.java, it, data) as TypeDeclaration
        }

    }
}