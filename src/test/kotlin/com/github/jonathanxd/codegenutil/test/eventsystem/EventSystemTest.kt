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
package com.github.jonathanxd.codegenutil.test.eventsystem

import com.github.jonathanxd.codeapi.CodeSource
import com.github.jonathanxd.codeapi.Types
import com.github.jonathanxd.codeapi.base.CodeModifier
import com.github.jonathanxd.codeapi.bytecode.classloader.CodeClassLoader
import com.github.jonathanxd.codeapi.bytecode.processor.BytecodeGenerator
import com.github.jonathanxd.codeapi.factory.*
import com.github.jonathanxd.codeapi.helper.invokePrintlnStr
import com.github.jonathanxd.codeapi.util.codeType
import com.github.jonathanxd.codeapi.util.conversion.extend
import com.github.jonathanxd.codeapi.util.conversion.toInvocation
import com.github.jonathanxd.codeapi.util.conversion.toLiteral
import com.github.jonathanxd.codegenutil.CodeGen
import com.github.jonathanxd.codegenutil.implementer.Implementer
import com.github.jonathanxd.codegenutil.property.Property
import com.github.jonathanxd.codegenutil.property.PropertySystem
import org.junit.Test
import java.lang.reflect.Method

class EventSystemTest {

    @Test
    fun test() {
        val klass = EventSystemTest::class.java
        val cl = genEventListener(klass, klass.getDeclaredMethod("simpleListener", EventA::class.java))

        val instance = cl.getConstructor(klass).newInstance(this)

        (instance as Generated).call()
        (instance as EventListener).onEvent(EventA())
    }

    fun simpleListener(event: EventA) {
        println("Name: ${event.getName()}")
    }

    fun genEventListener(klass: Class<*>, methodToInvoke: Method): Class<*> {
        val typeDeclaration = classDec()
                .modifiers(CodeModifier.PUBLIC)
                .qualifiedName("com.GenListener")
                .superClass(Types.OBJECT)
                .build()
                .extend(EventListener::class.java)
                .extend(Generated::class.java)


        val declaration = createCodeGen(klass, methodToInvoke).visit(typeDeclaration)

        val gen = BytecodeGenerator().process(declaration)



        return loader.define(gen)
    }

    companion object {
        val loader = CodeClassLoader()

        fun createCodeGen(klass: Class<*>, methodToInvoke: Method): CodeGen {

            val codeGen = CodeGen()

            codeGen.install(PropertySystem(
                    Property(name = "listener", type = klass.codeType)
            ))

            codeGen.install(Implementer { method ->
                return@Implementer when (method.name) {
                    "onEvent" -> {
                        method.builder().body(CodeSource.fromVarArgs(returnValue(method.returnType, methodToInvoke.toInvocation(
                                null,
                                accessThisField(klass.codeType, "listener"),
                                listOf(method.parameters[0].let {
                                    val access = accessVariable(it.type, it.name)
                                    val cast = cast(Event::class.java, methodToInvoke.parameterTypes[0], access)
                                    return@let cast
                                })
                        )))).build()
                    }
                    "call" -> {
                        method.builder().body(CodeSource.fromVarArgs(
                                invokePrintlnStr("Call".toLiteral()!!)
                        )).build()
                    }
                    else -> method
                }
            })

            return codeGen
        }
    }

}