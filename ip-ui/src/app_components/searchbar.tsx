"use client"
import { Input } from "@/components/ui/input";
import { Search } from "lucide-react";
import "./css/searchbar.css";
import React, { useState } from 'react';


export default function SearchBar() {

    const [isFocused, setIsFocused] = useState(false);

    const handleFocus = () => {
      setIsFocused(true);
    };
  
    const handleBlur = () => {
      setIsFocused(false);
    };
  
    

  return (
    <div className={`flex rounded-full  search border bg-gray-100   ${isFocused ? 'focused dark:border-white' : 'unfocused'}`}
     onFocus={handleFocus} onBlur={handleBlur}>
      <label htmlFor="">
        <Search size={20} strokeWidth={1} className="my-3 mx-2 " color="#757171" />
      </label>
      <input type="text" placeholder="Search..." className="focus:outiline-none h-8  mt-1 mb-1 bg-gray-100" />
    </div>
  );
}
