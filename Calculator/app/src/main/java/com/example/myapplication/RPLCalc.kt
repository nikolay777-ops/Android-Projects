package com.example.myapplication

import android.util.Log
import java.util.Stack
import ch.obermuhlner.math.big.BigDecimalMath.*
import kotlinx.coroutines.withTimeout

import java.math.BigDecimal
import java.math.MathContext

enum class Priority(val pr: Pair<Int, String>){
    BRACKET(Pair(3, "(")),
    POWER(Pair(6, "^")),
    MULTIPLY(Pair(5, "*")),
    DEL(Pair(5, "/")),
    PLUS(Pair(4, "+")),
    MINUS(Pair(4, "-")),
    SINUS(Pair(7, "sn")),
    COSINUS(Pair(7, "cs")),
    TANG(Pair(7, "t")),
    COTANG(Pair(7, "ct")),
    LOGARITHM(Pair(7, "ln")),
    SQUARE(Pair(7, "st")),
    FACTORIAL(Pair(7, "ft"))
}

class RPLCalc {

    companion object{


        @JvmStatic
        private fun parsingToRPL(expr: String): MutableList<String>{
            val symbolStack : Stack<String> = Stack<String>()
            val result : MutableList<String> = mutableListOf()
            var num : String = String()
            var token : String? = null
            for (i in expr.indices){
                var data = expr[i].toString()
                if (isSymbol(data)){
                    if(num.isNotEmpty()){
                        result.add(num)
                        num = ""
                    }
                    if(symbolStack.isNotEmpty()){
                        if(data == ")"){
                            while(symbolStack.isNotEmpty() && symbolStack.peek() != "("){
                                result.add(symbolStack.pop())
                            }
                            symbolStack.pop()
                        }
                        else if(checkPriority(data, symbolStack.peek()) == 1 || data == "("){
                            symbolStack.push(data)
                        }
                        else{
                            while(symbolStack.isNotEmpty() && checkPriority(data, symbolStack.peek()) != 1){
                                result.add(symbolStack.pop())
                            }
                            symbolStack.push(data)
                        }
                    }
                    else{
                        symbolStack.push(data)
                    }
                }
                else if(data == "s" || data == "c" || data == "t" || data == "l" || data == "f"){
                    token = if(i+4 < expr.length){
                        getFuncToken(expr.slice(i..i+4))
                    } else{
                        null
                    }
                    if (data == "s" || data == "t"){
                        if(i != 0 && expr[i-1] == 'o'){
                            continue
                        }
                    }
                    if (token != null){
                        if (symbolStack.isEmpty() || (checkPriority(token, symbolStack.peek()) == 1)){

                            symbolStack.push(token)
                        }
                        else{
                            while(symbolStack.isNotEmpty() && checkPriority(token, symbolStack.peek()) != 1){
                                result.add(symbolStack.pop())
                            }
                            symbolStack.push("(")
                            symbolStack.push(data)
                        }
                    }
                }
                else if (isNum(data)){
                    num = num.plus(data)
                }
            }
            if(num.isNotEmpty()){
                result.add(num)
            }
            if(symbolStack.isNotEmpty()){
                while(symbolStack.isNotEmpty()){
                    result.add(symbolStack.pop())
                }
            }
            return result
        }
        @JvmStatic
         fun isSymbol(data: String) : Boolean{
            if (data == "(" || data == ")" || data == "^"
                || data == "*" || data == "/" || data == "+" || data == "-"){
                return true
            }
            return false
        }

        @JvmStatic
        fun isOperation(data: String) : Boolean{
            if (data == "^" || data == "^2" || data == "*" || data == "/" || data == "+" || data == "-"){
                return true
            }
            return false
        }

        @JvmStatic
        private fun checkPriority(firstSymbol: String, secondSymbol: String) : Int{
            var firstPair : Pair<Int, String> = Pair(0, "")
            var secondPair : Pair<Int, String> = Pair(0, "")
            for (priority in Priority.values()){
                if (firstSymbol == priority.pr.second){
                    firstPair = priority.pr
                }
                if (secondSymbol == priority.pr.second){
                    secondPair = priority.pr
                }
            }
            if (firstPair.first > secondPair.first){
                return 1
            }
            else if (firstPair.first < secondPair.first){
                return -1
            }
            return 0
        }

        @JvmStatic
        fun calculate(expression: String): BigDecimal {
            val rplResult = parsingToRPL(expression)
            Log.d("RPL result", rplResult.toString())
            return  calculateFromRPL(rplResult)
        }

        @JvmStatic
        private fun getFuncToken(data: String) : String?{
            if(data.contains("sin")){
                return "sn"
            }
            else if(data.contains("cos")){
                return "cs"
            }
            else if(data.contains("tan")){
                return "t"
            }
            else if(data.contains("cot")){
                return "ct"
            }
            else if(data.contains("ln")){
                return "ln"
            }
            else if(data.contains("sqrt")){
                return "st"
            }
            else if(data.contains("fact")){
                return "ft"
            }
            return null
        }

        @JvmStatic
        private fun isNum(data: String) : Boolean{
            if (data >= "0" && data <= "9"){
                return true
            }
            else if (data == "."){
                return true
            }
            return false
        }

        @JvmStatic
        private fun isBinaryOperator(data: String) : Boolean{
            val operators = Priority.values().slice(1..5)
            for (op in operators){
                if(data == op.pr.second){
                    return true
                }
            }
            return false
        }

        @JvmStatic
        private fun isUnaryOperator(data: String) : Boolean{
            val operators = Priority.values().slice(6..12)
            for (op in operators){
                if(data == op.pr.second){
                    return true
                }
            }
            return false
        }

        @JvmStatic
        private fun calculateFromRPL(expr: MutableList<String>) : BigDecimal {
            var result: BigDecimal? = null
            var varExpr = expr
            var index = 0
            var slice: MutableList<String>
            while(varExpr.size != 1){
                for (i in varExpr.indices){
                    if (isBinaryOperator(varExpr[i])) {
                        slice = varExpr.slice(i-2..i).toMutableList()
                        index = i - 2
                        result = calculateFromNum(slice)
                        if (result == (4799.145421).toBigDecimal()){
                            return (4799.145421).toBigDecimal()
                        }
                        for (elem in slice){
                            varExpr.remove(elem)
                        }
//                        varExpr = varExpr.drop(i+1).toMutableList()
                    }
                    else if(isUnaryOperator(varExpr[i])){
                        result = if (i != 0){
                            slice = varExpr.slice(i-1..i).toMutableList()
                            calculateFromNum(slice)
                        } else{
                            slice = varExpr.slice(0..1).toMutableList()
                            calculateFromNum(slice)
                        }
                        index = i - 1
                        for (elem in slice){
                            varExpr.remove(elem)
                        }
//                        varExpr = varExpr.drop(i+1).toMutableList()
                    }
                    if (result != null){
                        varExpr.add(index, result.toPlainString())
                        result = null
                        break
                    }
                }
            }

            return varExpr[0].toBigDecimal()
        }

        @JvmStatic
        private fun calculateFromNum(expr: List<String>) : BigDecimal{
            var num1 : BigDecimal = (-1.0).toBigDecimal()
            var num2 : BigDecimal = (-1.0).toBigDecimal()
            val context = MathContext(100)
            val result: BigDecimal
            if(expr.size == 3){
                num1 = expr[0].toBigDecimal()
                num2 = expr[1].toBigDecimal()
            }
            else if(expr.size == 2){
                num1 = expr[0].toBigDecimal()
            }
            try {
                result = when(expr[expr.size-1]){
                    "+" -> num1.plus(num2)
                    "-" -> num1.minus(num2)
                    "*" -> num1.multiply(num2, context)
                    "/" -> num1.divide(num2, context)
                    "^" -> num1.pow(num2.toInt(), context)
                    "ft" -> factorial(num1, context)
                    "st" -> sqrt(num1, context)
                    "ln" -> log(num1, context)
                    "ct" -> cot(num1, context)
                    "t" -> tan(num1, context)
                    "cs" -> cos(num1, context)
                    "sn" -> sin(num1, context)
                    else -> return (-225.15).toBigDecimal()
                }
            }catch (e: ArithmeticException){
                return if (e.message.equals("Division by zero")){
                    (4799.145421).toBigDecimal()
                }else{
                    (-225.15).toBigDecimal()
                }
            }
            return result
        }
    }
}