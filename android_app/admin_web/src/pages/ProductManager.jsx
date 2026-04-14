import React, { useState, useEffect } from 'react';
import { db, storage } from '../firebaseConfig';
import {
  collection,
  addDoc,
  getDocs,
  updateDoc,
  deleteDoc,
  doc,
  query,
  where
} from 'firebase/firestore';
import { ref, uploadBytes, getDownloadURL } from 'firebase/storage';
import { Plus, Edit2, Trash2, Search, X, Image as ImageIcon } from 'lucide-react';

const ProductManager = () => {
  const [products, setProducts] = useState([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [currentProduct, setCurrentProduct] = useState(null);

  // Form states
  const [name, setName] = useState('');
  const [price, setPrice] = useState('');
  const [category, setCategory] = useState('Cà phê');
  const [description, setDescription] = useState('');
  const [imageFile, setImageFile] = useState(null);

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {
    const querySnapshot = await getDocs(collection(db, "products"));
    const prods = querySnapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
    setProducts(prods);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      let imageUrl = currentProduct?.image || '';

      if (imageFile) {
        const storageRef = ref(storage, `products/${Date.now()}_${imageFile.name}`);
        await uploadBytes(storageRef, imageFile);
        imageUrl = await getDownloadURL(storageRef);
      }

      const productData = {
        name,
        price: Number(price),
        category,
        description,
        image: imageUrl,
        updatedAt: new Date()
      };

      if (currentProduct) {
        await updateDoc(doc(db, "products", currentProduct.id), productData);
      } else {
        await addDoc(collection(db, "products"), { ...productData, createdAt: new Date() });
      }

      resetForm();
      fetchProducts();
      setIsModalOpen(false);
    } catch (error) {
      alert("Lỗi: " + error.message);
    }
    setLoading(false);
  };

  const resetForm = () => {
    setName('');
    setPrice('');
    setCategory('Cà phê');
    setDescription('');
    setImageFile(null);
    setCurrentProduct(null);
  };

  const handleDelete = async (id) => {
    if (window.confirm("Bạn có chắc chắn muốn xóa sản phẩm này?")) {
      await deleteDoc(doc(db, "products", id));
      fetchProducts();
    }
  };

  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-6">
        <div className="relative">
          <Search className="absolute left-3 top-2.5 text-gray-400" size={20} />
          <input
            type="text"
            placeholder="Tìm tên sản phẩm..."
            className="pl-10 pr-4 py-2 border rounded-lg focus:ring-2 focus:ring-orange-400 outline-none w-80"
          />
        </div>
        <button
          onClick={() => { resetForm(); setIsModalOpen(true); }}
          className="bg-orange-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 hover:bg-orange-700 transition-colors"
        >
          <Plus size={20} /> Thêm món mới
        </button>
      </div>

      <div className="bg-white rounded-xl shadow-sm border overflow-hidden">
        <table className="w-full text-left text-sm">
          <thead className="bg-gray-50 text-gray-500 font-semibold uppercase">
            <tr>
              <th className="px-6 py-4">Hình ảnh</th>
              <th className="px-6 py-4">Tên sản phẩm</th>
              <th className="px-6 py-4">Danh mục</th>
              <th className="px-6 py-4">Giá tiền</th>
              <th className="px-6 py-4 text-center">Thao tác</th>
            </tr>
          </thead>
          <tbody className="divide-y">
            {products.map((product) => (
              <tr key={product.id} className="hover:bg-gray-50 transition-colors">
                <td className="px-6 py-4">
                  <img src={product.image || 'https://via.placeholder.com/50'} alt="" className="w-12 h-12 rounded-lg object-cover border" />
                </td>
                <td className="px-6 py-4 font-medium text-gray-800">{product.name}</td>
                <td className="px-6 py-4">
                  <span className="bg-orange-100 text-orange-700 px-2 py-1 rounded text-xs font-semibold uppercase">{product.category}</span>
                </td>
                <td className="px-6 py-4 font-bold">{product.price.toLocaleString()}đ</td>
                <td className="px-6 py-4">
                  <div className="flex justify-center gap-3">
                    <button
                      onClick={() => {
                        setCurrentProduct(product);
                        setName(product.name);
                        setPrice(product.price);
                        setCategory(product.category);
                        setDescription(product.description);
                        setIsModalOpen(true);
                      }}
                      className="p-2 text-blue-600 hover:bg-blue-50 rounded-lg"
                    >
                      <Edit2 size={18} />
                    </button>
                    <button
                      onClick={() => handleDelete(product.id)}
                      className="p-2 text-red-600 hover:bg-red-50 rounded-lg"
                    >
                      <Trash2 size={18} />
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Modal Add/Edit */}
      {isModalOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl w-full max-w-md overflow-hidden">
            <div className="p-6 border-b flex justify-between items-center">
              <h3 className="text-xl font-bold">{currentProduct ? 'Sửa sản phẩm' : 'Thêm sản phẩm mới'}</h3>
              <button onClick={() => setIsModalOpen(false)}><X size={24} className="text-gray-400" /></button>
            </div>
            <form onSubmit={handleSubmit} className="p-6 space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Tên món</label>
                <input required value={name} onChange={e => setName(e.target.value)} className="w-full border p-2 rounded-lg outline-none focus:ring-2 focus:ring-orange-400" />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Giá (VNĐ)</label>
                  <input required type="number" value={price} onChange={e => setPrice(e.target.value)} className="w-full border p-2 rounded-lg outline-none focus:ring-2 focus:ring-orange-400" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Danh mục</label>
                  <select value={category} onChange={e => setCategory(e.target.value)} className="w-full border p-2 rounded-lg outline-none focus:ring-2 focus:ring-orange-400">
                    <option>Cà phê</option>
                    <option>Trà trái cây</option>
                    <option>Đá xay</option>
                    <option>Bánh ngọt</option>
                  </select>
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Mô tả</label>
                <textarea rows="3" value={description} onChange={e => setDescription(e.target.value)} className="w-full border p-2 rounded-lg outline-none focus:ring-2 focus:ring-orange-400"></textarea>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Hình ảnh</label>
                <input type="file" onChange={e => setImageFile(e.target.files[0])} className="w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:bg-orange-50 file:text-orange-700 hover:file:bg-orange-100" />
              </div>
              <button
                type="submit"
                disabled={loading}
                className="w-full bg-orange-600 text-white py-3 rounded-xl font-bold hover:bg-orange-700 transition-colors mt-4 disabled:bg-gray-400"
              >
                {loading ? 'Đang lưu...' : (currentProduct ? 'Cập nhật' : 'Thêm món')}
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default ProductManager;
