import {  useSelector } from "react-redux/es/hooks/useSelector";
import { stateModel } from "./Models";
import { ReactNode, useState } from "react";
// type vs interface
// type is closed aka can not be extended by reassigning, but interface can 
// be extended by reassigning the interface

type userType = {
  name:string,
  id:number
}

const User = () => {
  const [user,setUser]=useState<userType>({} as userType) // or use useState<userType | null>
  //the above code is for when we are absolutely certain no null value would be present.
  const clickHandler = (user:userType) => {
    setUser(user)
  }

  return <div>
    <button onClick={()=>{clickHandler(user)}}></button>
  </div>
}

type headercomponent = {
  //passing component as child props.
  children:ReactNode
}

type ButtonProps = {
  handleClick : ()=> void
}
interface state extends stateModel{
  id:number
}
const listofPersonel = useSelector((state:state)=> state.token)
interface Person{
  name:string
}
interface Person{
  age:number
}//redeclared and extended

type BirdType = {
  wings: 2;
};
type Owl = { nocturnal: true } & BirdType;

interface BirdInterface {
  wings: 2;
}
type Robin = { nocturnal: false } & BirdInterface;

interface people extends Person{
  dob : number
}

const Rohit : people ={
  dob:3232,
  name:"Rohit",
  age:35
}
let gathering:Person = {
  name:'Shinu',age:32
}
console.log('no way')
const greeting: string = 'hello';
  const hero: [string, number] = ["hello", 36];

  enum Color {
    hello = 50,
    well,
    straight,
  }

  console.log(Color.well);

const Hello = () => {
  const age: number = 3;
  return "hello";
};

let pat : unknown = 10;
(pat as string).toUpperCase()

let b;         let a = 10;
b=10;       
    // a='string'throws error here
b='string'
b=true

let c = 30;

let multiType : string | number;   //providing different types for constant. also called union types
multiType = 63;
multiType = 'works'

function add(num1:number,num2:number):number{
    return num1 + num2
}

interface person{    //can be used instead of defining type person:{id:number}
    name:string,
    id:number,
    workflow?:boolean,
    allocation:string
}

const crowd : person = {name:'Test',id:36,allocation:'working'};




