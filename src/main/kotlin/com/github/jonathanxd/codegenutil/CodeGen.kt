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
package com.github.jonathanxd.codegenutil

import com.github.jonathanxd.codeapi.CodePart
import com.github.jonathanxd.codeapi.CodeSource
import com.github.jonathanxd.codeapi.base.*
import com.github.jonathanxd.codeapi.modify.visit.VisitManager
import com.github.jonathanxd.codegenutil.visitor.CodeSourceVisitor
import com.github.jonathanxd.codegenutil.visitor.TypeVisitor

class CodeGen {

    val visitManager: VisitManager<CodePart> = CodeGenVisitManager()
    private val installedModules = mutableListOf<Module>()

    init {
        this.visitManager.let {
            it.register(TypeDeclaration::class.java, TypeVisitor)
            it.registerSuper(ClassDeclaration::class.java, TypeVisitor)
            it.registerSuper(InterfaceDeclaration::class.java, TypeVisitor)
            it.registerSuper(EnumDeclaration::class.java, TypeVisitor)
            it.registerSuper(AnnotationDeclaration::class.java, TypeVisitor)

            it.register(CodeSource::class.java, CodeSourceVisitor)
        }
    }

    fun install(module: Module) {

        this.installedModules.forEach {
            if (!it.isCompatible(module))
                throw IllegalArgumentException("Module ${module.name} ($module) is not compatible with ${it.name} ($it)!!!")
        }

        module.setup(this)

        this.installedModules.add(module)
    }

    @Suppress("UNCHECKED_CAST")
    fun <R : CodePart> gen(codePart: R): R {
        return this.visitManager.visit(codePart) as R
    }
}