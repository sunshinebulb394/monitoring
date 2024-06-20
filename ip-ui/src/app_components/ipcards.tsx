import {NotebookText} from 'lucide-react';

interface IpCard {

    cardName: string;
    total: number;
}

interface ResponseObj {
    message: string,
    status: number,
    data: any,
    timestamp: string
}

async function getData() {
    "use server"
        const res = await fetch(`${process.env.BASE_URL}/stats/ip`,
        // { next: { revalidate: 300 } }
    );
    const jsonData: ResponseObj = await res.json();
    console.log(jsonData);
    return jsonData;

}

export default async function IpCards() {

    const ipCards: IpCard[] = (await getData()).data;
    console.log(ipCards)

    const card = (ipCard: IpCard) => {
        let title, iconColor;

        switch (ipCard.cardName) {
            case 'totalips':
                title = 'Total Ips';
                iconColor = 'dark:text-blue-500';
                break;
            case 'disabledips':
                title = 'Disabled Ips';
                iconColor = 'text-destructive';
                break;
            case 'enabledips':
                title = 'Enabled Ips';
                iconColor = 'text-green-500 dark:text-green-700';
                break;
            default:
                title = '';
                iconColor = '';
        }

        return (
            <div key={ipCard.cardName} className="border rounded-lg bg-background p-5 grid grid-cols-2 gap-y-4  h-full row-span-12">
                <span className="scroll-m-20 text-xl font-semibold tracking-tight col-start-1">
                    {title}
                </span>
                <NotebookText className={`h-6 w-7 ${iconColor} col-start-2`} />
                <div className="block col-span-2">
                    {ipCard.total}
                </div>
            </div>
        );
    };

    return (
        <div className="grid xs:grid-rows-6 md:grid-cols-4 xs:row-span-5 md:row-span-2 xs:gap-1 md:gap-x-5 ">
            {ipCards.map(ipCard => card(ipCard))}
        </div>
    );
}

