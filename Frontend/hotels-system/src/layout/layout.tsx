import { CustomFooter } from '@/components/custom/CustomFooter'
import CustomHeader from '@/components/custom/CustomHeader'
import { ScrollToTop } from '@/components/custom/ScrollTop'
import { UserContext } from '@/context/UserContext'
import { MyReservationsModal } from '@/reservations/components/ReservationsUserModal'
import useGetReservationsByUser from '@/reservations/hooks/useGetReservationsByUser'
import { use, useState } from 'react'
import { Outlet } from 'react-router'

export const Layout = () => {


    const [isReservationsModalOpen, setIsReservationsModalOpen] = useState(false)


    const { user } = use(UserContext);

    const { data: reservations } = useGetReservationsByUser(user ? user.id : 0);


    return (
        <div className="flex flex-col min-h-screen bg-background">
            <CustomHeader onReservationsClick={() => setIsReservationsModalOpen(true)}></CustomHeader>
            <ScrollToTop></ScrollToTop>
            <main className="flex-1 p-0">
                <Outlet />
            </main>
            <CustomFooter></CustomFooter>
            <MyReservationsModal
                isOpen={isReservationsModalOpen}
                onClose={() => setIsReservationsModalOpen(false)}
                reservations={reservations ? reservations : []}
            />
        </div>
    )
}

export default Layout
