import CustomHeader from '@/components/custom/CustomHeader'
import { ScrollToTop } from '@/components/custom/ScrollTop'
import { UserContext } from '@/context/UserContext'
import { MyReservationsModal } from '@/reservations/components/ReservationsUserModal'
import useGetReservationsByUser from '@/reservations/hooks/useGetReservationsByUser'
import { use, useState } from 'react'
import { Outlet } from 'react-router'

export const Layout = () => {


    const [isReservationsModalOpen, setIsReservationsModalOpen] = useState(false)

    {/**falta el id del usuario */ }
    const { user } = use(UserContext);

    const { data: reservations } = useGetReservationsByUser(user ? user.id : 0);


    return (
        <div className="min-h-screen bg-background">
            <CustomHeader onReservationsClick={() => setIsReservationsModalOpen(true)}></CustomHeader>
            <ScrollToTop></ScrollToTop>
            <main className="p-0">
                <Outlet />
            </main>
            <MyReservationsModal
                isOpen={isReservationsModalOpen}
                onClose={() => setIsReservationsModalOpen(false)}
                reservations={reservations ? reservations : []}
            />
        </div>
    )
}

export default Layout
