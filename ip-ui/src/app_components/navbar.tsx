import DarkMode from "./dark-mode";
import SearchBar from "./searchbar";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import './css/searchbar.css'
import Link from 'next/link'
import Slidebar from "@/app_components/slidebar";
import NotificationBell from "./notificationbell";

export default function sidebar() {
  return (
      <nav className=" bg-accent h-full grid grid-cols-12  gap-2 bg-white dark:bg-slate-900  ">
        <ul className="xs:hidden md:flex nav-container col-span-6 row-span-11  flex justify-center items-center  ">
          <li className="nav-item p-4 hover:text-gray-400 dark:hover:text-gray-400  text-md font-medium leading-none">
            <Link href="/dashboard">Dashboard</Link>

          </li>
          <li className="nav-item p-4 hover:text-gray-400 dark:hover:text-gray-400  text-md font-medium leading-none">
            <Link href="/dashboard">Analytics</Link>
          </li>
          <li className="nav-item p-4  hover:text-gray-400 dark:hover:text-gray-400  text-md font-medium leading-none">
            <Link href="/dashboard">Settings</Link>
          </li>
        </ul>

        <div className="xs:col-span-12 p-1 md:col-span-6 row-span-11  flex justify-center items-center  ">
          <div className="xs:hidden md:inline w-14 ">
            <DarkMode/>
          </div>
          <div className="xs:inline md:hidden  w-14 pl-2">
            <Slidebar/>
          </div>
          <div className="flex md:flex-initial md:w-96  xs:flex-1  justify-end pe-5">
            {/* <SearchBar/> */}
            <NotificationBell/>
          </div>
          <div className=" w-16 pl-2">
            <Avatar>
              <AvatarImage src="https://github.com/shadcn.png" alt="@shadcn"/>
              <AvatarFallback>CN</AvatarFallback>
            </Avatar>
          </div>
        </div>


        {/*<div className="col-span-6  flex md:items-center md:justify-center  xs:row-span-10 border">*/}
        {/*  <div className=" w-14">*/}
        {/*    <DarkMode/>*/}
        {/*  </div>*/}
        {/*  <div className=" md:flex-initial md:w-96  xs:flex-1 ">*/}
        {/*    <SearchBar/>*/}
        {/*  </div>*/}
        {/*  <div className=" w-16 pl-2">*/}
        {/*    <Avatar>*/}
        {/*      <AvatarImage src="https://github.com/shadcn.png" alt="@shadcn" />*/}
        {/*      <AvatarFallback>CN</AvatarFallback>*/}
        {/*    </Avatar>*/}
        {/*  </div>*/}
        {/*</div>*/}
      </nav>
  );
}
