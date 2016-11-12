/**
 *      CodeGenUtil - Code generation utilities built on top of CodeAPI
 *
 *         The MIT License (MIT)
 *
 *      Copyright (c) 2016 JonathanxD <https://github.com/JonathanxD/>
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
package com.github.jonathanxd.codegenutil.test

import com.github.jonathanxd.codeapi.CodeAPI
import com.github.jonathanxd.codeapi.MutableCodeSource
import com.github.jonathanxd.codeapi.classloader.CodeClassLoader
import com.github.jonathanxd.codeapi.conversions.extend
import com.github.jonathanxd.codeapi.gen.visit.bytecode.BytecodeGenerator
import com.github.jonathanxd.codeapi.helper.Predefined
import com.github.jonathanxd.codeapi.interfaces.TypeDeclaration
import com.github.jonathanxd.codeapi.literals.Literals
import com.github.jonathanxd.codegenutil.CodeGen
import com.github.jonathanxd.codegenutil.implementer.Implementer
import org.junit.Test
import java.lang.reflect.Modifier

class CodeGenTest {

    @Test
    fun test() {
        val myDeclaration: TypeDeclaration = CodeAPI.aClassBuilder()
                .withModifiers(Modifier.PUBLIC)
                .withQualifiedName("com.Test")
                .withBody(MutableCodeSource())
                .build()

        var extend = myDeclaration.extend(MyClass::class.java)

        val gen = CodeGen()

        gen.install(Implementer { method ->
            when (method.name) {
                "a" -> method.setBody(CodeAPI.sourceOfParts(Predefined.invokePrintlnStr(Literals.STRING("A"))))
                else -> method
            }
        })

        extend = gen.gen(extend)

        val classes = BytecodeGenerator().gen(extend)

        val loader = CodeClassLoader()

        val defined = loader.define(classes)

        val myClass = defined.newInstance() as MyClass
        myClass.a()
    }

}