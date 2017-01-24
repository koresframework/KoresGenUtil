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

import com.github.jonathanxd.codeapi.CodeAPI
import com.github.jonathanxd.codeapi.CodeSource
import com.github.jonathanxd.codeapi.MutableCodeSource
import com.github.jonathanxd.codeapi.Types
import com.github.jonathanxd.codeapi.builder.ClassDeclarationBuilder
import com.github.jonathanxd.codeapi.bytecode.classloader.CodeClassLoader
import com.github.jonathanxd.codeapi.bytecode.gen.BytecodeGenerator
import com.github.jonathanxd.codeapi.common.CodeModifier
import com.github.jonathanxd.codeapi.conversions.createInvocation
import com.github.jonathanxd.codeapi.conversions.extend
import com.github.jonathanxd.codeapi.conversions.toLiteral
import com.github.jonathanxd.codeapi.helper.Predefined
import com.github.jonathanxd.codeapi.util.codeType
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
        val typeDeclaration = ClassDeclarationBuilder.builder()
                .withModifiers(CodeModifier.PUBLIC)
                .withQualifiedName("com.GenListener")
                .withSuperClass(Types.OBJECT)
                .withBody(MutableCodeSource())
                .build()
                .extend(EventListener::class.java)
                .extend(Generated::class.java)


        val declaration = createCodeGen(klass, methodToInvoke).gen(typeDeclaration)

        val gen = BytecodeGenerator().gen(declaration)

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
                        method.builder().withBody(CodeSource.fromVarArgs(CodeAPI.returnValue(method.returnType, methodToInvoke.createInvocation(
                                CodeAPI.accessThisField(klass.codeType, "listener"),
                                listOf(method.parameters[0].let {
                                    val access = CodeAPI.accessLocalVariable(it.type, it.name)
                                    val cast = CodeAPI.cast(Event::class.java, methodToInvoke.parameterTypes[0], access)
                                    return@let cast
                                })
                        )))).build()
                    }
                    "call" -> {
                        method.builder().withBody(CodeSource.fromVarArgs(
                                Predefined.invokePrintlnStr("Call".toLiteral()!!)
                        )).build()
                    }
                    else -> method
                }
            })

            return codeGen
        }
    }

}