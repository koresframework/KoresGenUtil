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

import com.github.jonathanxd.kores.base.*
import com.github.jonathanxd.koresgenutil.CodeGen
import com.github.jonathanxd.koresgenutil.Module

class PropertySystem(vararg val properties: Property) : Module {

    override val name: String = "PropertySystem"

    override fun setup(codeGen: CodeGen) {
        codeGen.visitManager.register(TypeDeclaration::class.java, TypeDeclarationVisitor(this.properties))
        codeGen.visitManager.registerSuper(ClassDeclaration::class.java, TypeDeclarationVisitor(this.properties))
        codeGen.visitManager.registerSuper(EnumDeclaration::class.java, TypeDeclarationVisitor(this.properties))
        codeGen.visitManager.registerSuper(InterfaceDeclaration::class.java, TypeDeclarationVisitor(this.properties))
        codeGen.visitManager.registerSuper(AnnotationDeclaration::class.java, TypeDeclarationVisitor(this.properties))

        codeGen.visitManager.register(ConstructorsHolder::class.java, ConstructorsHolderVisitor(this.properties))
    }
}