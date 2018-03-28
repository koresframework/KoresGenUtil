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
import com.github.jonathanxd.kores.Instructions
import com.github.jonathanxd.kores.base.ConstructorsHolder
import com.github.jonathanxd.kores.base.KoresModifier
import com.github.jonathanxd.kores.factory.accessVariable
import com.github.jonathanxd.kores.factory.constructorDec
import com.github.jonathanxd.kores.factory.parameter
import com.github.jonathanxd.kores.factory.setThisFieldValue
import com.github.jonathanxd.kores.modify.visit.PartVisitor
import com.github.jonathanxd.kores.modify.visit.VisitManager

class ConstructorsHolderVisitor(val properties: Array<out Property>) :
    PartVisitor<ConstructorsHolder> {

    override fun visit(
        koresPart: ConstructorsHolder,
        data: TypedData,
        visitManager: VisitManager<*>
    ): ConstructorsHolder {
        return koresPart.builder()
            .constructors(koresPart.constructors + constructorDec()
                .modifiers(KoresModifier.PUBLIC)
                .parameters(this.properties.map { parameter(type = it.type, name = it.name) })
                .body(Instructions.fromIterable(
                    this.properties.map {
                        setThisFieldValue(
                            it.type,
                            it.name,
                            accessVariable(it.type, it.name)
                        )
                    }
                ))
                .build())
            .build()
    }
}