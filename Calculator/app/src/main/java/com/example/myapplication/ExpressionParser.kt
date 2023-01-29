package com.example.myapplication

import android.widget.Toast
import kotlin.math.exp

enum class Functions(var value: String){
    SINUS("sin"),
    COSINUS("cos"),
    TANG("tan"),
    COTANG("cot"),
    LOGARITHM("ln"),
    SQUARE("sqrt"),
    FACTORIAL("fact")
}

class ExpressionParser {

    companion object{

        @JvmStatic
        fun dotParser(text : String) : Boolean{
            if (text.contains('.')){
                return false
            }
            return true
        }

        @JvmStatic
        fun getNum(text: String): String{
            for (i in text.length-1 downTo 0){
                if (RPLCalc.isSymbol(text[i].toString())){
                    return text.slice(i until text.length)
                }
            }
            return text
        }

        @JvmStatic
        private fun isBracket(sybmol: String): Boolean{
            if (sybmol == "(" || sybmol == ")"){
                return true
            }
            return false
        }

        @JvmStatic
        fun checkSymbols(firstSymb: String, secondSymb: String): Boolean{
            if (RPLCalc.isSymbol(firstSymb) &&
                    RPLCalc.isSymbol(secondSymb)){
                if (isBracket(firstSymb) && isBracket(secondSymb)){
                    if (firstSymb == ")" && secondSymb == "("){
                        return false
                    }
                    return true
                }
                else if (firstSymb == secondSymb){
                    return false
                }
            }
            else if (firstSymb == "."){
                return false
            }
            return true
        }

        @JvmStatic
        fun checkBrackets(expr: String): Boolean{
            var offset = expr.indexOf("(")
            var count = 0
            while (offset != -1){
                count += 1
                offset = expr.indexOf("(", offset+1)
            }
            offset = expr.indexOf(")")
            while (offset != -1){
                count -= 1
                offset = expr.indexOf(")", offset+1)
            }
            if (count != 0){
                return false
            }
            return true
        }

        @JvmStatic
        fun deleteFun(expr: String, pos: Int): String{
            var i = pos
            if (isLetter(expr[pos])){
                var end: Int = findEndNest(expr,
                    findBracketNest(expr, pos))
                while (i != 0 && isLetter(expr[i])){
                    i -= 1
                }
                if (end == -1){
                    end = expr.indexOf(')')
                }
                val bracket = expr.indexOf('(', i+1)
                if(i != 0){
                    i+=1
                }
                return expr.removeRange(end..end).removeRange(i..bracket)
            }
            return expr
        }

        @JvmStatic
        private fun findEndNest(expr: String, nest: Int): Int{
            var i = 0
            var nestCopy = nest
            while(i < expr.length-1 && nestCopy != 0){
                if (expr[i] == ')'){
                    nestCopy -= 1
                }
                i += 1
            }
            if (i-1 != expr.length-1){
                return i-1
            }
            return -1
        }

        @JvmStatic
        private fun findBracketNest(expr: String, pos: Int): Int{
            var index = 0
            for (i in 0..pos){
                if (expr[i] == '('){
                    index += 1
                }
            }
            return index
        }

        @JvmStatic
        fun isLetter(letter: Char): Boolean{
            if(letter in 'a'..'z'){
                return true
            }
            return false
        }

        @JvmStatic
        fun isNumber(letter: Char): Boolean{
            if(letter >= '0' && letter <= '9'){
                return true
            }
            return false
        }

        @JvmStatic
        fun checkExpr(text: String): Boolean {
            if (text.isEmpty()) return true
            if (RPLCalc.isOperation(text[0].toString()) || RPLCalc.isOperation(text[text.length - 1].toString())) return false;
            if (text[0] == '.' || text[text.length - 1] == '.') return false;
            for (i in 1 until text.length) {
                if (RPLCalc.isOperation(text[i].toString()) && (RPLCalc.isOperation(text[i - 1].toString()) || RPLCalc.isOperation(
                        text[i + 1].toString()
                    ))
                ) return false;
                if (text[i] == ')' && text[i - 1] == '(') return false;
                if (text[i] == '.' && (!isNumber(text[i - 1]) || !isNumber(text[i + 1]))) return false;
            }
            return true
        }
    }
}