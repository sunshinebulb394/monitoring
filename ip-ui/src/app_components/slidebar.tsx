import {
    Sheet,
    SheetContent,
    SheetDescription,
    SheetHeader,
    SheetTitle,
    SheetTrigger,
} from "@/components/ui/sheet"
import { AlignJustify } from 'lucide-react';
import DarkMode from "./dark-mode";
import Link from "next/link";

export default function Slidebar(){

    return (
        <Sheet>
            <SheetTrigger>
                <AlignJustify />
            </SheetTrigger>
            <SheetContent side="left">
                <SheetHeader>
                    <SheetTitle><DarkMode/></SheetTitle>
                    <SheetDescription>
                     <ul>
                         <li className="nav-item p-4 hover:text-gray-400 dark:hover:text-gray-400  text-md font-medium leading-none">
                             <Link href="/dashboard">Dashboard</Link>

                         </li>
                         <li className="nav-item p-4 hover:text-gray-400 dark:hover:text-gray-400  text-md font-medium leading-none">
                             <Link href="/dashboard">Dashboard</Link>

                         </li>
                         <li className="nav-item p-4 hover:text-gray-400 dark:hover:text-gray-400  text-md font-medium leading-none">
                             <Link href="/dashboard">Dashboard</Link>

                         </li>
                     </ul>
                    </SheetDescription>
                </SheetHeader>
            </SheetContent>
        </Sheet>

    )

}